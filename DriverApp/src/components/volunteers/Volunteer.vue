<script setup>  
  defineProps(['volunteer'])
</script>

<template>
  <div v-if="volunteer">
    <p>{{ volunteer.name }}</p>
    <p>{{ volunteer.email }}</p>
  </div> 
</template>
