#!/bin/bash

# AI呼叫中心前台系统启动脚本

echo "🚀 启动AI呼叫中心前台系统..."

# 检查Node.js版本
node_version=$(node -v 2>/dev/null)
if [ $? -ne 0 ]; then
    echo "❌ 错误: 未安装Node.js，请先安装Node.js 16+"
    exit 1
fi

echo "📦 Node.js版本: $node_version"

# 检查npm版本
npm_version=$(npm -v 2>/dev/null)
if [ $? -ne 0 ]; then
    echo "❌ 错误: 未安装npm"
    exit 1
fi

echo "📦 npm版本: $npm_version"

# 检查是否存在node_modules
if [ ! -d "node_modules" ]; then
    echo "📥 安装依赖包..."
    npm install
    if [ $? -ne 0 ]; then
        echo "❌ 依赖安装失败"
        exit 1
    fi
    echo "✅ 依赖安装完成"
else
    echo "✅ 依赖已存在"
fi

# 检查后端服务是否运行
echo "🔍 检查后端服务..."

# 检查cc-api服务 (端口8080)
if curl -s http://localhost:8080/index/health > /dev/null 2>&1; then
    echo "✅ cc-api服务运行正常 (端口8080)"
else
    echo "⚠️  警告: cc-api服务未运行，请先启动后端服务"
fi

# 检查fs-api服务 (端口8081)
if curl -s http://localhost:8081/index/health > /dev/null 2>&1; then
    echo "✅ fs-api服务运行正常 (端口8081)"
else
    echo "⚠️  警告: fs-api服务未运行，请先启动后端服务"
fi

echo ""
echo "🌐 启动开发服务器..."
echo "📱 前台地址: http://localhost:3000"
echo "🔧 后台API: http://localhost:8080"
echo "📞 呼叫API: http://localhost:8081"
echo ""
echo "💡 默认登录账号: admin / 12345678"
echo ""

# 启动开发服务器
npm run dev


