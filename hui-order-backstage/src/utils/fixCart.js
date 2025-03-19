/**
 * 购物车修复工具
 * 这个脚本将检查和修复本地存储中的购物车数据
 */

// 要删除的商品名称
const ITEMS_TO_REMOVE = ['Smartphone X', 'Wireless Headphones'];

// 在控制台打印当前购物车状态
function printCartStatus() {
  try {
    const cartData = localStorage.getItem('cart');
    if (!cartData) {
      console.log('当前购物车为空');
      return;
    }
    
    const cart = JSON.parse(cartData);
    console.log('当前购物车状态:', cart);
    console.log('商品数量:', cart.length);
    
    // 列出所有商品
    cart.forEach((item, index) => {
      console.log(`${index + 1}. 商品ID: ${item.id}, 名称: ${item.name}, 数量: ${item.quantity}`);
    });
  } catch (error) {
    console.error('查看购物车时出错:', error);
  }
}

// 修复购物车 - 移除指定商品
function fixCart() {
  try {
    console.log('开始修复购物车...');
    
    // 获取购物车数据
    const cartData = localStorage.getItem('cart');
    if (!cartData) {
      console.log('购物车为空，无需修复');
      return;
    }
    
    // 解析购物车数据
    let cart = JSON.parse(cartData);
    console.log('修复前购物车商品数:', cart.length);
    
    // 备份原始购物车
    const originalCart = [...cart];
    
    // 移除指定商品
    const filteredCart = cart.filter(item => !ITEMS_TO_REMOVE.includes(item.name));
    
    // 检查是否有商品被移除
    const removedCount = originalCart.length - filteredCart.length;
    console.log(`已移除 ${removedCount} 件商品`);
    
    if (removedCount > 0) {
      // 保存修复后的购物车
      localStorage.setItem('cart', JSON.stringify(filteredCart));
      console.log('购物车已更新');
      
      // 输出被移除的商品
      const removedItems = originalCart.filter(item => ITEMS_TO_REMOVE.includes(item.name));
      console.log('被移除的商品:', removedItems);
    } else {
      console.log('未找到需要移除的商品');
    }
    
    console.log('购物车修复完成');
  } catch (error) {
    console.error('修复购物车时出错:', error);
  }
}

// 通过ID删除商品
function removeItemById(id) {
  try {
    // 获取购物车数据
    const cartData = localStorage.getItem('cart');
    if (!cartData) {
      console.log('购物车为空，无法删除商品');
      return false;
    }
    
    // 解析购物车数据
    let cart = JSON.parse(cartData);
    
    // 查找商品
    const index = cart.findIndex(item => item.id === id);
    
    if (index === -1) {
      console.log(`未找到ID为 ${id} 的商品`);
      return false;
    }
    
    // 记录删除的商品
    const removedItem = cart[index];
    
    // 从数组中删除
    cart.splice(index, 1);
    
    // 保存更新后的购物车
    localStorage.setItem('cart', JSON.stringify(cart));
    
    console.log(`已删除商品: ${removedItem.name} (ID: ${removedItem.id})`);
    return true;
  } catch (error) {
    console.error('通过ID删除商品时出错:', error);
    return false;
  }
}

// 强制清空购物车
function forceEmptyCart() {
  try {
    localStorage.removeItem('cart');
    localStorage.setItem('cart', JSON.stringify([]));
    console.log('购物车已强制清空');
    return true;
  } catch (error) {
    console.error('强制清空购物车时出错:', error);
    return false;
  }
}

// 执行检查
console.log('===== 购物车修复工具 =====');
console.log('购物车状态检查:');
printCartStatus();

console.log('\n===== 执行修复 =====');
fixCart();

console.log('\n===== 修复后购物车状态 =====');
printCartStatus();

// 提示用户如何使用此脚本
console.log('\n===== 使用指南 =====');
console.log('要删除特定ID的商品, 请在控制台执行:');
console.log('removeItemById("商品ID")');
console.log('\n要强制清空购物车, 请在控制台执行:');
console.log('forceEmptyCart()');

// 将函数暴露到全局作用域以便在控制台使用
window.printCartStatus = printCartStatus;
window.removeItemById = removeItemById;
window.forceEmptyCart = forceEmptyCart;
