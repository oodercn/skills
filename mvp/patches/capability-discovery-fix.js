// 修复 capability-discovery.js 统计数量显示问题
// 将第 238 行的硬编码 100 改为实际数量

// 原代码:
// scanStats.scanned = 100;

// 修复后的代码:
scanStats.scanned = data.total || caps.length;

// 同时修复 showEmptyResult 函数中的硬编码（第 260 行）
// 原代码:
// scanStats.scanned = 100;

// 修复后的代码:
scanStats.scanned = 0;
