<template>
  <button class="btn btn-primary" @click="clicked">{{text}}</button>
</template>

<script>
  export default {
    props: ['text'],
    methods: {
      clicked() {
        this.$emit('click', this)
      }
    }
  }
</script>

<style>

</style>
