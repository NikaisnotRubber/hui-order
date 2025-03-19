<template>
  <div class="not-found">
    <div class="not-found-content">
      <h1>404</h1>
      <h2>Page Not Found</h2>
      <p>The page you are looking for doesn't exist or has been moved.</p>
      <router-link to="/" class="btn">Go Home</router-link>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
  text-align: center;
  padding: 2rem;
}

.not-found-content {
  background: white;
  padding: 3rem;
  border-radius: 8px;
  box-shadow: var(--shadow);
  max-width: 500px;
}

h1 {
  font-size: 5rem;
  color: var(--primary-color);
  margin: 0;
}

h2 {
  margin-top: 0.5rem;
  margin-bottom: 1rem;
}

p {
  margin-bottom: 2rem;
  color: var(--text-light);
}

.btn {
  display: inline-block;
  padding: 0.75rem 1.5rem;
}
</style>
