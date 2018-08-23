<template>
  <div class="timeline-container">
    <build-timeline v-bind:items="builds" />
  </div>
</template>

<script>
  import BuildTimeline from '../components/BuildTimeline'

  export default {
    name: 'PageHeader',
    components: {BuildTimeline},
    props: ['builds']
  }
</script>

<style>
  .timeline-container {
    padding: 30px;
  }
</style>
