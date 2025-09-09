(function (root, factory) {
  if (typeof define === 'function' && define.amd) {
    define([], factory);
  } else if (typeof module === 'object' && module.exports) {
    module.exports = factory();
  } else {
    root.Voice9 = factory();
  }
}(typeof self !== 'undefined' ? self : this, function () {
  'use strict';

  class EventBus {
    constructor() {
      this._listeners = new Map();
    }

    on(type, handler) {
      if (!type || typeof handler !== 'function') return () => {};
      let set = this._listeners.get(type);
      if (!set) {
        set = new Set();
        this._listeners.set(type, set);
      }
      set.add(handler);
      return () => this.off(type, handler);
    }

    off(type, handler) {
      const set = this._listeners.get(type);
      if (!set) return;
      set.delete(handler);
      if (set.size === 0) this._listeners.delete(type);
    }

    emit(type, payload) {
      const set = this._listeners.get(type);
      if (!set || set.size === 0) return;
      for (const fn of set) {
        try { fn(payload); } catch (e) { /* ignore listener errors */ }
      }
    }
  }

  function buildWsUrl(config) {
    if (config.wsUrl) return config.wsUrl;
    const isHttps = (config.protocol || (typeof location !== 'undefined' ? location.protocol : 'http:')) === 'https:';
    const scheme = isHttps ? 'wss' : 'ws';
    const host = config.wsHost || (typeof location !== 'undefined' ? location.hostname : '127.0.0.1');
    const port = config.wsPort != null ? String(config.wsPort) : '7250';
    const path = (config.wsPath || 'ws').replace(/^\//, '');
    const token = encodeURIComponent(config.token || '');
    return `${scheme}://${host}:${port}/${path}?token=${token}`;
  }

  class Voice9 {
    constructor(options) {
      this._options = options || {};
      this._bus = new EventBus();
      this._ws = null;
      this._wsUrl = null;
      this._token = null;
      this._httpBase = this._options.httpBase || '/fs-api';
      this._heartbeatMs = this._options.heartbeatMs || 15000;
      this._hb = null;

      // Media elements (optional, for WebRTC use-cases)
      this._media = {
        remoteAudio: typeof document !== 'undefined' ? document.getElementById('remoteVideo') : null,
        peerAudio: typeof document !== 'undefined' ? document.getElementById('peerVideo') : null
      };
    }

    // Event API
    addEventListener(type, handler) { return this._bus.on(type, handler); }
    removeEventListener(type, handler) { this._bus.off(type, handler); }

    // Initialize using login result or explicit config
    // Accepts: token string OR { token, wsUrl, wsHost, wsPort, wsPath, protocol, httpBase }
    init(config) {
      const cfg = typeof config === 'string' ? { token: config } : (config || {});
      this._token = cfg.token || this._options.token || this._token;
      this._httpBase = cfg.httpBase || this._httpBase;
      this._wsUrl = buildWsUrl({
        protocol: cfg.protocol,
        wsUrl: cfg.wsUrl,
        wsHost: cfg.wsHost,
        wsPort: cfg.wsPort,
        wsPath: cfg.wsPath,
        token: this._token
      });
      this._openWebSocket();
      return this;
    }

    // WebSocket
    _openWebSocket() {
      if (!this._wsUrl) throw new Error('WebSocket url not set. Call init({ token, ... }) first.');
      try { if (this._ws) this._ws.close(); } catch (_) {}

      const ws = new (typeof WebSocket !== 'undefined' ? WebSocket : global.WebSocket)(this._wsUrl);
      this._ws = ws;

      ws.onopen = () => {
        this._startHeartbeat();
        // 完成鉴权后发送登录命令，触发后端 WS_LOGIN，便于后端返回初始化状态
        this._send({ cmd: 'login', time: Date.now() });
        this._bus.emit('open');
      };

      ws.onmessage = (ev) => {
        let data = ev && ev.data;
        try {
          const msg = JSON.parse(data);
          // 总线透传
          this._bus.emit('message', msg);
          // 根据 type 分发（例如 LOGIN、LOGOUT、pong 等）
          if (msg && msg.type) {
            this._bus.emit(String(msg.type).toLowerCase(), msg);
          }
        } catch (e) {
          // 非 JSON 消息
          this._bus.emit('message', { raw: data });
        }
      };

      ws.onclose = () => {
        this._stopHeartbeat();
        this._bus.emit('close');
      };

      ws.onerror = (err) => {
        this._bus.emit('error', err);
      };
    }

    _startHeartbeat() {
      this._stopHeartbeat();
      if (!this._heartbeatMs) return;
      this._hb = setInterval(() => {
        try {
          // 发送不含 cmd 的心跳，避免后端日志告警
          this._send({ type: 'ping', time: Date.now() });
        } catch (_) {}
      }, this._heartbeatMs);
    }

    _stopHeartbeat() {
      if (this._hb) {
        clearInterval(this._hb);
        this._hb = null;
      }
    }

    _send(payload) {
      if (!this._ws || this._ws.readyState !== 1) return;
      this._ws.send(JSON.stringify(payload));
    }

    // HTTP helper
    _post(url, body) {
      const headers = { 'Content-Type': 'application/json' };
      if (this._token) headers['token'] = this._token;
      return fetch(`${this._httpBase}${url}`, {
        method: 'POST',
        headers: headers,
        body: body ? JSON.stringify(body) : null
      }).then(r => r.json());
    }

    // Public API 映射（依据后端 /cti 与 WS_* 事件）
    setBusy(notReadyCode) {
      const body = typeof notReadyCode === 'number' ? { notReadyCode } : undefined;
      return this._post('/cti/agent/notReady', body);
    }

    setReady() {
      return this._post('/cti/agent/ready');
    }

    logout() {
      return this._post('/cti/agent/logout')
        .catch(() => {})
        .finally(() => {
          try { if (this._ws) this._ws.close(); } catch (_) {}
          this._bus.emit('logout', {});
        });
    }

    makeCall(called, options) {
      const opts = options || {};
      const body = {
        called: called,
        callType: opts.callType != null ? opts.callType : 2, // 默认手动外呼
        caller: opts.caller,
        callerDisplay: opts.callerDisplay,
        calledDisplay: opts.calledDisplay,
        uuid1: opts.uuid1,
        uuid2: opts.uuid2,
        followData: opts.followData
      };
      return this._post('/cti/call/makeCall', body);
    }

    acceptCall(params) {
      // 通过 WS 触发 WS_ANSWER
      const payload = { cmd: 'answer' };
      if (params && params.callId) payload.callId = params.callId;
      this._send(payload);
    }

    closeCall() {
      return this._post('/cti/call/hangup');
    }

    holdTalking() {
      return this._post('/cti/call/hold');
    }

    cancelHold() {
      return this._post('/cti/call/cancelHold');
    }

    mutePhone() {
      return this._post('/cti/call/readyMute');
    }

    cancelMute() {
      return this._post('/cti/call/cancelMute');
    }

    // 以下为咨询/转接相关（当前仓库未显式提供 WS 事件类，保留前端 API 以兼容现有调用）
    phoneTransfer(target) {
      this._send({ cmd: 'transfer', target });
    }

    phoneConsult(target) {
      this._send({ cmd: 'consult', target });
    }

    phoneConsultCancel() {
      this._send({ cmd: 'consult_cancel' });
    }

    phoneConsultTransfer(target) {
      this._send({ cmd: 'consult_transfer', target });
    }

    phoneConsultParty() {
      this._send({ cmd: 'consult_party' });
    }
  }

  return Voice9;
}));


