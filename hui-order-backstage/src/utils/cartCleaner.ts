/**
 * 购物车清理工具 - 用于删除特定商品
 */

// 根据商品名称从购物车中删除商品
export function removeItemsByName(itemNames: string[]): boolean {
  try {
    // 从 localStorage 获取购物车数据
    const cartData = localStorage.getItem('cart');
    
    if (!cartData) {
      console.log('购物车为空，无需删除');
      return true;
    }
    
    // 解析购物车数据
    const cartItems = JSON.parse(cartData);
    
    if (!Array.isArray(cartItems) || cartItems.length === 0) {
      console.log('购物车为空数组，无需删除');
      return true;
    }
    
    console.log('当前购物车商品:', cartItems.map(item => item.name));
    
    // 过滤掉要删除的商品
    const filteredItems = cartItems.filter(item => !itemNames.includes(item.name));
    
    // 保存回 localStorage
    localStorage.setItem('cart', JSON.stringify(filteredItems));
    
    console.log('删除后的购物车商品:', filteredItems.map(item => item.name));
    console.log(`已成功删除以下商品: ${itemNames.join(', ')}`);
    
    return true;
  } catch (error) {
    console.error('删除购物车商品时出错:', error);
    return false;
  }
}

// 执行删除操作
removeItemsByName(['Smartphone X', 'Wireless Headphones']);

console.log('购物车清理完成！');
