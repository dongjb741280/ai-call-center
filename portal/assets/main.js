const select = (selector, root = document) => root.querySelector(selector);
const selectAll = (selector, root = document) => [...root.querySelectorAll(selector)];

const menuToggle = select('.menu-toggle');
const navigation = select('.navigation');
function closeMenu() {
  menuToggle?.setAttribute('aria-expanded', 'false');
  menuToggle?.setAttribute('aria-label', '打开导航菜单');
  navigation?.classList.remove('open');
}
menuToggle?.addEventListener('click', () => {
  const open = menuToggle.getAttribute('aria-expanded') !== 'true';
  menuToggle.setAttribute('aria-expanded', String(open));
  menuToggle.setAttribute('aria-label', open ? '关闭导航菜单' : '打开导航菜单');
  navigation?.classList.toggle('open', open);
});
selectAll('.navigation a').forEach(link => link.addEventListener('click', closeMenu));
document.addEventListener('keydown', event => {
  if (event.key === 'Escape' && menuToggle?.getAttribute('aria-expanded') === 'true') {
    closeMenu();
    menuToggle.focus();
  }
});
document.addEventListener('click', event => {
  if (!event.target.closest('.header')) closeMenu();
});
window.matchMedia('(min-width: 701px)').addEventListener('change', closeMenu);

const scenarios = {
  service: {
    kicker: 'CUSTOMER SERVICE',
    title: '从“请稍候”到“我来帮您”',
    description: '把产品说明和常见问答放进知识库，AI 理解客户问题并组织回复，需要进一步处理时转接人工。',
    features: ['知识库检索，让回答有所依据', '业务工具调用，连接查询与办理', '保留人工服务出口'],
    source: '部署说明',
    messages: [
      ['user', '产品可以部署在我们自己的服务器吗？'],
      ['ai', '可以。呼叫中心与语音编排服务支持自主部署，语音识别和合成也可以本地运行。'],
      ['user', '我想和技术人员聊聊具体方案。'],
      ['ai', '好的，可以为您转接人工坐席继续沟通。']
    ],
    steps: [
      ['呼叫接入', '客户拨入服务号码', 'FreeSWITCH 接收呼叫，将会话交给语音编排服务。', '客户：“产品可以部署在自己的服务器吗？”'],
      ['识别与理解', '把声音转成可理解的需求', 'FunASR 处理整句语音，大模型结合当前对话识别客户的咨询意图。', '识别内容：产品私有化部署咨询'],
      ['知识与业务', '检索知识，组织回答', 'RAG 检索部署相关说明，大模型基于知识组织回复，TTS 将回复转为语音。', 'AI：“支持自主部署，我可以为您介绍配置要求。”'],
      ['服务完成', '需要时，让人工接续服务', '客户要求进一步咨询时，语音编排调用转人工工具，连接指定坐席分机。', 'AI：“好的，为您转接技术顾问继续沟通。”']
    ]
  },
  followup: {
    kicker: 'CUSTOMER FOLLOW-UP',
    title: '一次服务之后，关心仍在继续',
    description: '通过坐席外呼与任务管理组织客户回访，记录反馈和服务结果，为后续跟进提供依据。',
    features: ['按任务组织回访名单', '坐席主动外呼与通话记录', '通过业务集成沉淀客户反馈'],
    source: '回访流程示例',
    messages: [
      ['ai', '您好，这里是服务回访。方便了解一下您最近的使用体验吗？'],
      ['user', '整体体验不错，希望能了解后续的升级计划。'],
      ['ai', '谢谢您的反馈。我会记录需求，并安排进一步沟通。'],
      ['user', '好的，谢谢。']
    ],
    steps: [
      ['回访任务', '组织需要跟进的客户', '根据业务需求建立回访任务，准备客户名单、字段与服务话术。', '示例任务：产品使用情况回访'],
      ['坐席外呼', '由坐席发起回访电话', '坐席通过工作台或软电话发起呼叫，呼叫中心负责线路与通话控制。', '坐席：“方便了解一下最近的使用体验吗？”'],
      ['反馈记录', '把客户反馈留在业务流程中', '记录客户建议和跟进需求；如需自动同步 CRM，应对接业务接口。', '客户：“希望了解后续的升级计划。”'],
      ['后续跟进', '让服务有下一步', '结合通话记录和客户反馈安排后续沟通，回访流程需按实际业务集成。', '结果：登记产品升级咨询，安排专人跟进。']
    ]
  },
  collaboration: {
    kicker: 'HUMAN COLLABORATION',
    title: '让每一个复杂问题，都找到对的人',
    description: '客户希望人工协助，或 AI 无法继续处理时，通过转人工工具连接坐席，让服务回到专业团队。',
    features: ['AI 编排支持转人工工具', '呼叫中心提供技能组与 ACD 路由', '坐席状态与通话记录集中管理'],
    source: '人机协同流程示例',
    messages: [
      ['user', '我们的接入场景比较复杂，想和技术顾问详细沟通。'],
      ['ai', '理解您的需求，我为您转接人工坐席。'],
      ['user', '好的，请帮我转接。'],
      ['ai', '正在为您连接，请稍候。']
    ],
    steps: [
      ['客户需求', '识别需要人工参与的时刻', '客户提出转人工请求，或当前问题需要专业团队判断。', '客户：“我想和技术顾问详细沟通。”'],
      ['编排决策', 'AI 发起转人工动作', '语音编排层调用转人工工具，使用已配置的目标分机。', '动作：transfer_to_agent'],
      ['连接坐席', '由呼叫引擎完成转接', 'FreeSWITCH 执行转接。技能组分配需要与呼叫中心路由能力进行集成。', '状态：正在连接人工坐席'],
      ['人工服务', '由专业团队继续沟通', '人工坐席继续处理问题；跨系统上下文同步需接入对应业务接口。', '坐席：“您好，请问有什么可以帮您？”']
    ]
  },
  outbound: {
    kicker: 'SMART OUTBOUND',
    title: '主动触达，也有节奏、有边界',
    description: '以任务组织外呼名单，坐席经工作台主动外呼并记录结果；预测式外呼、号码频控与外呼合规为预留能力，需接入线路与策略配置。',
    features: ['按任务组织外呼名单', '坐席主动外呼与结果记录', '预测式外呼 · 预留'],
    source: '外呼流程示例',
    messages: [
      ['ai', '您好，这里是声枢外呼中心，方便耽误您两分钟吗？'],
      ['user', '可以，请讲。'],
      ['ai', '了解到您近期体验过我们的产品，想了解一下使用感受。'],
      ['user', '体验不错，希望后续的电话别太频繁。']
    ],
    steps: [
      ['外呼任务', '组织需要触达的客户', '按业务目标建立外呼任务，准备客户名单、话术与拨打策略。', '示例任务：产品体验回访名单'],
      ['坐席外呼', '由坐席发起呼叫', '坐席通过工作台与软电话发起呼出，呼叫中心负责线路与通话控制。', '坐席：“您好，这里是声枢外呼中心。”'],
      ['预测外呼', '按策略自动分配', '预留能力：按坐席空闲与接听率预测拨号，减少等待并提升效率。', '预留：预测式外呼需接入拨号策略'],
      ['合规与结果', '有边界地触达与沉淀', '记录外呼结果与客户偏好；号码频控、黑名单与外呼时段限制为预留合规能力。', '结果：登记体验反馈与拨打偏好']
    ]
  }
};
let selectedScenario = 'service';
function renderScenario(key) {
  const data = scenarios[key];
  if (!data || !select('#scenario-panel')) return;
  selectedScenario = key;
  select('#scenario-kicker').textContent = data.kicker;
  select('#scenario-title').textContent = data.title;
  select('#scenario-description').textContent = data.description;
  const features = select('#scenario-features');
  features.replaceChildren(...data.features.map(text => {
    const item = document.createElement('li');
    item.textContent = text;
    return item;
  }));
  const messages = select('#scenario-messages');
  messages.replaceChildren();
  data.messages.forEach(([role, text], index) => {
    const bubble = document.createElement('p');
    bubble.className = role === 'user' ? 'bubble-user' : 'bubble-ai';
    bubble.textContent = text;
    messages.append(bubble);
    if (index === 1) {
      const source = document.createElement('span');
      source.className = 'conversation-source';
      source.textContent = '✧ 参考：' + data.source;
      messages.append(source);
    }
  });
  selectAll('.scenario-tab').forEach(tab => {
    const active = tab.dataset.scenario === key;
    tab.classList.toggle('active', active);
    tab.setAttribute('aria-selected', String(active));
    tab.tabIndex = active ? 0 : -1;
  });
  select('#scenario-panel').setAttribute('aria-labelledby', 'tab-' + key);
  const assistantLabel = select('.conversation-header strong');
  const labels = { followup: '声枢 · 坐席回访', outbound: '声枢 · 智能外呼' };
  assistantLabel.textContent = labels[key] || '声枢 AI 助手';
}
const tabs = selectAll('.scenario-tab');
tabs.forEach(tab => {
  tab.addEventListener('click', () => renderScenario(tab.dataset.scenario));
  tab.addEventListener('keydown', event => {
    const keys = ['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'Home', 'End'];
    if (!keys.includes(event.key)) return;
    event.preventDefault();
    const direction = ['ArrowUp', 'ArrowLeft'].includes(event.key) ? -1 : 1;
    let next = (tabs.indexOf(tab) + direction + tabs.length) % tabs.length;
    if (event.key === 'Home') next = 0;
    if (event.key === 'End') next = tabs.length - 1;
    renderScenario(tabs[next].dataset.scenario);
    tabs[next].focus();
  });
});
const tabMedia = window.matchMedia('(max-width: 950px)');
function updateTabOrientation() {
  select('.scenario-tabs')?.setAttribute('aria-orientation', tabMedia.matches ? 'horizontal' : 'vertical');
}
tabMedia.addEventListener('change', updateTabOrientation);
updateTabOrientation();

const dialog = select('#demo-dialog');
let demoStep = 0;
let demoTrigger = null;
function updateDemo() {
  const steps = scenarios[selectedScenario].steps;
  const [label, title, text, quote] = steps[demoStep];
  select('#demo-step-label').textContent = String(demoStep + 1).padStart(2, '0') + ' / ' + label;
  select('#demo-step-title').textContent = title;
  select('#demo-step-text').textContent = text;
  select('#demo-step-quote').textContent = quote;
  selectAll('#demo-steps li').forEach((item, index) => {
    item.textContent = steps[index][0];
    item.classList.toggle('current', index === demoStep);
    item.classList.toggle('complete', index < demoStep);
    if (index === demoStep) item.setAttribute('aria-current', 'step');
    else item.removeAttribute('aria-current');
  });
  select('#demo-next').textContent = demoStep === steps.length - 1 ? '完成演示 ✓' : '下一步 →';
}
selectAll('[data-demo-open]').forEach(trigger => trigger.addEventListener('click', () => {
  demoStep = 0;
  demoTrigger = trigger;
  updateDemo();
  dialog.showModal();
  document.body.classList.add('modal-open');
  select('#demo-next').focus();
}));
select('[data-demo-close]')?.addEventListener('click', () => dialog.close());
dialog?.addEventListener('click', event => {
  const rect = dialog.getBoundingClientRect();
  if (event.target === dialog && (event.clientX < rect.left || event.clientX > rect.right || event.clientY < rect.top || event.clientY > rect.bottom)) dialog.close();
});
dialog?.addEventListener('close', () => {
  document.body.classList.remove('modal-open');
  demoTrigger?.focus();
});
select('#demo-next')?.addEventListener('click', () => {
  if (demoStep === scenarios[selectedScenario].steps.length - 1) dialog.close();
  else { demoStep++; updateDemo(); }
});
select('#demo-restart')?.addEventListener('click', () => { demoStep = 0; updateDemo(); });

selectAll('[data-copy]').forEach(button => button.addEventListener('click', async () => {
  const code = button.closest('.code-block')?.querySelector('code');
  try {
    await navigator.clipboard.writeText(code.textContent);
    button.textContent = '已复制';
  } catch {
    const range = document.createRange();
    range.selectNodeContents(code);
    const selection = window.getSelection();
    selection.removeAllRanges();
    selection.addRange(range);
    button.textContent = '已选中，请复制';
  }
  setTimeout(() => { button.textContent = '复制'; }, 2000);
}));
selectAll('[data-year]').forEach(element => { element.textContent = new Date().getFullYear(); });
