<template>
  <div class="timeline-container">
    <build-timeline></build-timeline>
  </div>
</template>

<script>
  import BuildTimeline from '../components/BuildTimeline'
  export default {
    name: 'PageHeader',
    components: {BuildTimeline}
  }
</script>

<style>
  .timeline-container {
    padding: 30px;
  }
</style>
