<template>
  <div class="not-found">
    <div class="error-container">
      <div class="error-code">404</div>
      <h1 class="error-title">找不到頁面</h1>
      <p class="error-message">抱歉，您訪問的頁面不存在或已被移除。</p>
      <router-link to="/" class="btn btn-primary">返回首頁</router-link>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f8f9fa;
}

.error-container {
  text-align: center;
  padding: 40px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
}

.error-code {
  font-size: 6rem;
  font-weight: 700;
  color: #4a69bd;
  line-height: 1;
  margin-bottom: 20px;
}

.error-title {
  font-size: 2rem;
  margin: 0 0 20px;
  color: #2c3e50;
}

.error-message {
  margin-bottom: 30px;
  color: #7f8c8d;
  font-size: 1.1rem;
}

.btn-primary {
  display: inline-block;
  background-color: #4a69bd;
  color: white;
  padding: 12px 24px;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: background-color 0.3s;
}

.btn-primary:hover {
  background-color: #3d5aa1;
}
</style>
