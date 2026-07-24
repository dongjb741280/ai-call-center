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
      try { fn(payload); } catch (e) { /* ignore */ }
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

export default class CallSdk {
  constructor(options) {
    this._options = options || {};
    this._bus = new EventBus();
    this._ws = null;
    this._wsUrl = null;
    this._token = null;
    this._httpBase = this._options.httpBase || '/fs-api';
    this._heartbeatMs = this._options.heartbeatMs || 15000;
    this._hb = null;
    this._loginType = null;
    this._workType = null;

    this._media = {
      remoteAudio: typeof document !== 'undefined' ? document.getElementById('remoteVideo') : null,
      peerAudio: typeof document !== 'undefined' ? document.getElementById('peerVideo') : null
    };
  }

  // Event API
  addEventListener(type, handler) { return this._bus.on(type, handler); }
  removeEventListener(type, handler) { this._bus.off(type, handler); }

  init(config) {
    const cfg = typeof config === 'string' ? { token: config } : (config || {});
    this._token = cfg.token || this._options.token || this._token;
    this._loginType = cfg.loginType != null ? cfg.loginType : this._options.loginType;
    this._workType = cfg.workType != null ? cfg.workType : this._options.workType;
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

  _openWebSocket() {
    if (!this._wsUrl) throw new Error('WebSocket url not set. Call init({ token, ... }) first.');
    try { if (this._ws) this._ws.close(); } catch (_) {}

    const ws = new WebSocket(this._wsUrl);
    this._ws = ws;

    ws.onopen = () => {
      this._startHeartbeat();
      this._send({ cmd: 'login', time: Date.now(), loginType: this._loginType, workType: this._workType });
      this._bus.emit('open');
    };

    ws.onmessage = (ev) => {
      let data = ev && ev.data;
      try {
        const msg = JSON.parse(data);
        this._bus.emit('message', msg);
        if (msg && msg.type) {
          this._bus.emit(String(msg.type).toLowerCase(), msg);
        }
      } catch (e) {
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

  _post(url, body) {
    const headers = { 'Content-Type': 'application/json' };
    if (this._token) headers['token'] = this._token;
    return fetch(`${this._httpBase}${url}`, {
      method: 'POST',
      headers: headers,
      body: body ? JSON.stringify(body) : null
    }).then(r => r.json());
  }

  // ---- 坐席状态 ----
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

  // ---- 通话操作 ----
  makeCall(called, options) {
    const opts = options || {};
    const body = {
      called: called,
      callType: opts.callType != null ? opts.callType : 2,
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

  // ---- 转接 / 咨询 ----
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

  // ---- 班长监控 ----
  phoneListen(target) {
    this._send({ cmd: 'listen', target });
  }

  phoneInsert(target) {
    this._send({ cmd: 'insert', target });
  }

  phoneWhisper(target) {
    this._send({ cmd: 'whisper', target });
  }
}
