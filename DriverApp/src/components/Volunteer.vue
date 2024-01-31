<script setup>  
  defineProps(['volunteer'])
</script>

<template>
  <div>
    <p>{{ volunteer.name }}</p>
    <p>{{ volunteer.email }}</p>
  </div> 
</template>
