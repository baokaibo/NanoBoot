// Internationalization / 国际化
var i18n = {
  zh: {
    dashboard: '仪表盘',
    beans: 'Beans',
    routes: '路由',
    api: 'API Tester',
    graph: '依赖图',
    config: '配置',
    overview: '概览',
    totalBeans: 'Bean总数',
    totalRoutes: '路由总数',
    uptime: '运行时间',
    seconds: '秒',
    systemInfo: '系统信息',
    version: '版本',
    javaVersion: 'Java版本',
    osName: '操作系统',
    memory: '内存使用',
    used: '已使用',
    free: '空闲',
    total: '总计',
    beanList: 'Bean列表',
    className: '类名',
    scope: '作用域',
    singleton: '单例',
    prototype: '原型',
    routeList: '路由列表',
    method: '方法',
    path: '路径',
    handler: '处理器',
    apiTester: 'API测试器',
    requestMethod: '请求方法',
    requestPath: '请求路径',
    requestBody: '请求体 (JSON)',
    send: '发送请求',
    response: '响应',
    responseTime: '响应时间',
    dependencyGraph: '依赖关系图',
    configuration: '配置信息',
    key: '键',
    value: '值',
    sendRequest: '发送请求...',
    error: '错误',
    language: '语言',
    english: 'English',
    chinese: '中文',
    clickToTest: '点击自动填充',
    quickTest: '快速测试',
    noRoutes: '暂无路由'
  },
  en: {
    dashboard: 'Dashboard',
    beans: 'Beans',
    routes: 'Routes',
    api: 'API Tester',
    graph: 'Dependency Graph',
    config: 'Config',
    overview: 'Overview',
    totalBeans: 'Total Beans',
    totalRoutes: 'Total Routes',
    uptime: 'Uptime',
    seconds: 'seconds',
    systemInfo: 'System Info',
    version: 'Version',
    javaVersion: 'Java Version',
    osName: 'OS',
    memory: 'Memory Usage',
    used: 'Used',
    free: 'Free',
    total: 'Total',
    beanList: 'Bean List',
    className: 'Class Name',
    scope: 'Scope',
    singleton: 'Singleton',
    prototype: 'Prototype',
    routeList: 'Route List',
    method: 'Method',
    path: 'Path',
    handler: 'Handler',
    apiTester: 'API Tester',
    requestMethod: 'Method',
    requestPath: 'Path',
    requestBody: 'Request Body (JSON)',
    send: 'Send Request',
    response: 'Response',
    responseTime: 'Response Time',
    dependencyGraph: 'Dependency Graph',
    configuration: 'Configuration',
    key: 'Key',
    value: 'Value',
    sendRequest: 'Sending request...',
    error: 'Error',
    language: 'Language',
    english: 'English',
    chinese: '中文',
    clickToTest: 'Click to test',
    quickTest: 'Quick Test',
    noRoutes: 'No routes'
  }
};

var currentLang = 'zh';
var routeListData = [];

// Toggle language / 切换语言
function toggleLanguage() {
  currentLang = currentLang === 'zh' ? 'en' : 'zh';
  updatePageText();
  localStorage.setItem('nanoboot-lang', currentLang);
}

// Safe textContent setter
function safeSetText(selector, text) {
  var el = document.querySelector(selector);
  if (el) el.textContent = text;
}

// Safe innerHTML setter
function safeSetHTML(selector, html) {
  var el = document.querySelector(selector);
  if (el) el.innerHTML = html;
}

// Update all text on page / 更新页面所有文本
function updatePageText() {
  var t = i18n[currentLang];
  
  // Tab buttons
  safeSetHTML('[data-tab="dashboard"]', '<i class="fas fa-th-large"></i> ' + t.dashboard);
  safeSetHTML('[data-tab="beans"]', '<i class="fas fa-cube"></i> ' + t.beans);
  safeSetHTML('[data-tab="routes"]', '<i class="fas fa-route"></i> ' + t.routes);
  safeSetHTML('[data-tab="api"]', '<i class="fas fa-paper-plane"></i> ' + t.api);
  safeSetHTML('[data-tab="graph"]', '<i class="fas fa-project-diagram"></i> ' + t.graph);
  safeSetHTML('[data-tab="config"]', '<i class="fas fa-cog"></i> ' + t.config);
  
  // Dashboard - update stat card labels
  var statCards = document.querySelectorAll('#dashboard .stat-card .label');
  if (statCards[0]) statCards[0].textContent = t.totalBeans;
  if (statCards[1]) statCards[1].textContent = t.totalRoutes;
  if (statCards[2]) statCards[2].textContent = t.uptime;
  if (statCards[3]) statCards[3].textContent = t.systemInfo;
  
  // Dashboard h2
  safeSetHTML('#dashboard h2', '<i class="fas fa-th-large"></i> ' + t.overview);
  
  // Beans
  safeSetHTML('#beans h2', '<i class="fas fa-cube"></i> ' + t.beanList);
  var beanTable = document.querySelector('#beans table');
  if (beanTable) {
    var thead = beanTable.querySelector('thead tr');
    if (thead) thead.innerHTML = '<th>' + t.className + '</th><th>' + t.scope + '</th>';
  }
  
  // Routes
  safeSetHTML('#routes h2', '<i class="fas fa-route"></i> ' + t.routeList);
  var routeTable = document.querySelector('#routes table');
  if (routeTable) {
    var thead = routeTable.querySelector('thead tr');
    if (thead) thead.innerHTML = '<th>' + t.method + '</th><th>' + t.path + '</th><th>' + t.handler + '</th>';
  }
  
  // API Tester
  safeSetHTML('#api h2', '<i class="fas fa-paper-plane"></i> ' + t.apiTester);
  safeSetText('#api label[for="requestMethod"]', t.requestMethod + ':');
  safeSetText('#api label[for="requestPath"]', t.requestPath + ':');
  safeSetText('#api label[for="requestBody"]', t.requestBody + ':');
  safeSetText('#api button', t.send);
  safeSetText('#api .response-label', t.response);
  
  // Quick test section
  safeSetHTML('#quickTestTitle', '<i class="fas fa-bolt"></i> ' + t.quickTest);
  
  // Graph
  safeSetHTML('#graph h2', '<i class="fas fa-project-diagram"></i> ' + t.dependencyGraph);
  
  // Config
  safeSetHTML('#config h2', '<i class="fas fa-cog"></i> ' + t.configuration);
  var configTable = document.querySelector('#config table');
  if (configTable) {
    var thead = configTable.querySelector('thead tr');
    if (thead) thead.innerHTML = '<th>' + t.key + '</th><th>' + t.value + '</th>';
  }
  
  // Language toggle button text
  var langBtn = document.getElementById('langToggle');
  if (langBtn) langBtn.textContent = currentLang === 'zh' ? t.english : t.chinese;
  
  // Update quick test buttons
  renderQuickTestButtons();
}

// Render quick test buttons from route list
function renderQuickTestButtons() {
  var t = i18n[currentLang];
  var container = document.getElementById('quickTestContainer');
  if (!container) return;
  
  if (routeListData.length === 0) {
    container.innerHTML = '<p style="color:#888">' + t.noRoutes + '</p>';
    return;
  }
  
  var html = '';
  routeListData.forEach(function(route) {
    var methodClass = route.method.toUpperCase();
    html += '<button class="quick-btn" data-method="' + route.method + '" data-path="' + route.path + '">';
    html += '<span class="method-badge ' + methodClass + '">' + route.method + '</span>';
    html += '<span class="path">' + route.path + '</span>';
    html += '</button>';
  });
  container.innerHTML = html;
  
  // Add click handlers
  container.querySelectorAll('.quick-btn').forEach(function(btn) {
    btn.addEventListener('click', function() {
      var method = this.getAttribute('data-method');
      var path = this.getAttribute('data-path');
      document.getElementById('requestMethod').value = method;
      document.getElementById('requestPath').value = path;
      // Auto focus and select path for easy editing
      document.getElementById('requestPath').focus();
    });
    btn.title = t.clickToTest;
  });
}

// Tab switching
document.getElementById('tabContainer').addEventListener('click', function(e) {
  var btn = e.target.closest('.tab-btn');
  if (!btn) return;
  var tabId = btn.getAttribute('data-tab');
  if (!tabId) return;
  document.querySelectorAll('.tab-content').forEach(function(t) { t.classList.remove('active'); });
  document.querySelectorAll('.tab-btn').forEach(function(b) { b.classList.remove('active'); });
  document.getElementById(tabId).classList.add('active');
  btn.classList.add('active');
  if(tabId === 'graph') renderGraph();
});

// Send request
async function sendRequest(){
  var method = document.getElementById('requestMethod').value;
  var path = document.getElementById('requestPath').value;
  var body = document.getElementById('requestBody').value;
  var responseArea = document.getElementById('responseArea');
  var startTime = Date.now();
  var t = i18n[currentLang];
  
  if (!path) {
    responseArea.textContent = 'Please enter a path';
    responseArea.style.color = '#f44336';
    return;
  }
  
  // Add base URL if path doesn't start with http
  if (!path.startsWith('http://') && !path.startsWith('https://')) {
    var baseUrl = window.location.origin;
    path = baseUrl + path;
  }
  
  responseArea.textContent = t.sendRequest;
  responseArea.style.color = '#888';
  
  try {
    var options = {method: method, headers: {'Content-Type': 'application/json'}};
    if(method !== 'GET' && body) options.body = body;
    
    var res = await fetch(path, options);
    var text = await res.text();
    var time = Date.now() - startTime;
    
    // Try to format JSON
    try {
      var json = JSON.parse(text);
      text = JSON.stringify(json, null, 2);
    } catch(e) {}
    
    responseArea.textContent = 'Status: ' + res.status + ' ' + res.statusText + '\nTime: ' + time + 'ms\n\n' + text;
    responseArea.style.color = res.ok ? '#4CAF50' : '#f44336';
    document.getElementById('responseTime').textContent = t.responseTime + ': ' + time + 'ms';
  } catch(e) {
    responseArea.textContent = t.error + ': ' + e.message;
    responseArea.style.color = '#f44336';
  }
}

// Render dependency graph
function renderGraph(){
  var container = document.getElementById('graphContainer');
  var beans = ['ApplicationContext', 'BeanFactory', 'Environment', 'BeanPostProcessor', 'Controller', 'Service', 'Repository', 'Component'];
  var deps = [
    {from: 'ApplicationContext', to: 'BeanFactory'},
    {from: 'ApplicationContext', to: 'Environment'},
    {from: 'BeanFactory', to: 'BeanPostProcessor'},
    {from: 'Controller', to: 'Service'},
    {from: 'Service', to: 'Repository'},
    {from: 'Service', to: 'Component'}
  ];
  
  var width = container.clientWidth - 40;
  var height = container.clientHeight - 60;
  var cx = width / 2;
  var cy = height / 2;
  var radius = Math.min(width, height) / 2 - 60;
  
  var svg = '<svg width="' + width + '" height="' + height + '" style="font-family:Consolas,sans-serif">';
  
  // Draw connections
  deps.forEach(function(d) {
    var i1 = beans.indexOf(d.from);
    var i2 = beans.indexOf(d.to);
    if(i1 >= 0 && i2 >= 0){
      var a1 = (i1 / beans.length) * 2 * Math.PI - Math.PI/2;
      var a2 = (i2 / beans.length) * 2 * Math.PI - Math.PI/2;
      var x1 = cx + radius * Math.cos(a1);
      var y1 = cy + radius * Math.sin(a1);
      var x2 = cx + radius * Math.cos(a2);
      var y2 = cy + radius * Math.sin(a2);
      svg += '<defs><marker id="arrowhead" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto"><polygon points="0 0, 10 3.5, 0 7" fill="#667eea"/></marker></defs>';
      svg += '<line x1="' + x1 + '" y1="' + y1 + '" x2="' + x2 + '" y2="' + y2 + '" stroke="#667eea" stroke-width="2" marker-end="url(#arrowhead)" opacity="0.6"/>';
    }
  });
  
  // Draw nodes
  beans.forEach(function(bean, i) {
    var angle = (i / beans.length) * 2 * Math.PI - Math.PI/2;
    var x = cx + radius * Math.cos(angle);
    var y = cy + radius * Math.sin(angle);
    var colors = ['#667eea','#764ba2','#f093fb','#f5576c','#4facfe','#00f2fe','#43e97b','#38f9d7'];
    var color = colors[i % colors.length];
    svg += '<circle cx="' + x + '" cy="' + y + '" r="35" fill="' + color + '" opacity="0.9"/>';
    svg += '<text x="' + x + '" y="' + y + '" fill="white" text-anchor="middle" dy="5" font-size="11" font-weight="600">' + bean + '</text>';
  });
  
  svg += '</svg>';
  container.innerHTML = svg;
}

// Fetch route list from server
async function fetchRoutes() {
  try {
    var baseUrl = window.location.origin;
    var res = await fetch(baseUrl + '/nanoboot/routes');
    if (res.ok) {
      var text = await res.text();
      try {
        routeListData = JSON.parse(text);
      } catch(e) {
        // Try to parse as route list from HTML
        routeListData = [];
      }
    }
  } catch(e) {
    console.log('Could not fetch routes:', e);
  }
  renderQuickTestButtons();
}

// Initialize
document.addEventListener('DOMContentLoaded', function(){
  // Load saved language preference
  var savedLang = localStorage.getItem('nanoboot-lang');
  if (savedLang) {
    currentLang = savedLang;
  }
  
  // Language toggle
  var langBtn = document.getElementById('langToggle');
  if (langBtn) {
    langBtn.addEventListener('click', toggleLanguage);
  }
  
  // Update initial text
  updatePageText();
  
  // Enter key for API request
  var pathInput = document.getElementById('requestPath');
  if (pathInput) {
    pathInput.addEventListener('keypress', function(e){
      if(e.key === 'Enter') sendRequest();
    });
  }
  
  // Send button click
  var sendBtn = document.querySelector('#api button');
  if (sendBtn) {
    sendBtn.addEventListener('click', sendRequest);
  }
  
  // Fetch routes for quick test
  fetchRoutes();
});
