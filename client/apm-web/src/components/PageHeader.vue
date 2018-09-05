<template>
  <build-timeline :items="builds" :selection="build" @click-item="itemClicked"/>
</template>

<script>
  import BuildTimeline from '../components/BuildTimeline'

  export default {
    name: 'PageHeader',
    components: {BuildTimeline},
    props: ['builds', 'build'],
    methods: {
      itemClicked(item) {
        const oldUuid = this.$props.build.uuid;
        const newUuid = item.$props.uuid

        if (oldUuid == newUuid) {
          return ;
        }
        console.log('Current uuid: ' + oldUuid + ', New uuid: ' + newUuid);
        this.$emit('select-build', newUuid)
      }
    }
  }
</script>

<style>
  .timeline-container {
    padding: 30pt;
  }
</style>
