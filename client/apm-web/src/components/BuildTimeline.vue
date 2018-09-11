<template>
  <div class="timeline">
    <div class="horizontal-line"></div>
    <div class="item-container">
      <timeline-item v-for="item in items" :key="item.uuid"
                     :selected="selection.uuid == item.uuid" v-bind="item" @click="itemClicked"/>
    </div>
  </div>
</template>

<script>
  import Vue from 'vue'

  const TimelineItem = {
    name: 'TimelineItem',
    props: ['uuid', 'state', 'timestamp', 'selected'],
    template:
`<div class="timeline-item">
  <div v-if="selected" class="selection" />
  <div class="icon" @click="clicked"/>
  <div class="status" @click="clicked">
    <img v-if="0 == state" src="/static/okay-icon.svg" class="status-text">
    <img v-if="1 == state" src="/static/build-icon.svg" class="status-text">
    <img v-if="2 == state" src="/static/test-icon.svg" class="status-text">
    <span class="timestamp">{{timestampDisplay(timestamp)}}</span>
  </div>
</div>`,
    methods: {
      timestampDisplay(ts) {
        let seconds = Vue.moment().diff(ts, 'seconds');
        if (seconds < 60) {
          return seconds + ' seconds ago';
        }
        return Vue.moment(ts).fromNow();
      },

      clicked() {
        this.$emit('click', this)
      }
    }
  };

  Vue.component(TimelineItem);
  export default {
    name: 'BuildTimeline',
    props: ['items', 'selection'],
    components: { TimelineItem: TimelineItem },
    methods: {
      itemClicked(item) {
        console.log(item + ' clicked')
        this.$emit("click-item", item)
      }
    }
  }
</script>

<style>
  .timeline {
    position: relative;
    height: 70pt;
    width: 745pt;
    background-color: #f9f9f9;
    overflow: hidden;
  }
  .horizontal-line {
    position: absolute;
    top: 24pt;
    left: 70pt;
    width: 653pt;
    height: 2pt;
    background-color: #d4d6da;
    border: solid 1pt #d4d6da;
  }

  .timeline-item {
    display: inline-block;
    width: 110pt;
    height: 70pt;
    position: relative;
  }

  .timeline-item .selection {
    position:absolute;
    top:18pt;
    left: 66pt;
    width: 15pt;
    height: 15pt;
    border-radius: 50%;
    background-color: #D2D3D5;
    border: solid 1pt rgba(0, 0, 0, .097);
  }

  .timeline-item.error .selection {
    background-color: #F3CFCF;
    border: solid 1pt rgba(215, 61, 62, .3);
  }

  .timeline-item .icon {
    position:absolute;
    top:23pt;
    left: 71pt;
    width: 5pt;
    height: 5pt;
    border-radius: 50%;
    background-color: #171f28;
    cursor: pointer;
  }

  .timeline-item.error .icon {
    background-color: #d73d3e;
  }
  .timeline-item .status {
    position: absolute;
    cursor: pointer;
    top: 32pt;
    left: 68pt;
    width: 150pt;
  }

  .timeline-item .status-text {
    position: relative;
    width: 40pt;
    height: 12pt;
  }

  .timeline-item .timestamp {
    position: relative;
    height: 9pt;
    font-family: NanumSquareOTFR;
    font-size: 8pt;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: normal;
    color: #000000;
  }

</style>
