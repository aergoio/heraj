<template>
  <div class="row">
    <b-dropdown id="target-selector" :text="(null == text)?'Select target...':text" class="m-md-2">
      <b-dropdown-item v-for="item in targets" :key="item" @click="itemSelected(item)">{{item}}</b-dropdown-item>
    </b-dropdown>

    <b-input-group>
      <b-dropdown text="Select" variant="info" slot="prepend">
        <b-dropdown-item v-for="item in addresses" :key="item.name" @click="addressSelected(item)">{{item.name}}</b-dropdown-item>
      </b-dropdown>
      <b-form-input v-model="address" placeholder="Input your privatekey."></b-form-input>
    </b-input-group>

    <b-button @click="loadClicked" :disabled="null === text || '' === this.address">Load</b-button>
    <b-button @click="runClicked" :disabled="0 === parameters.length">Run</b-button>
  </div>
</template>

<script>
  export default {
    props: ['targets'],
    data() {
      return {
        text: null,
        address: '',
        addresses: [],
        parameters: []
      }
    },
    methods: {
      itemSelected(item) {
        console.log('Selected');
        this.$data.text = item;
      },
      addressSelected(item) {
        this.$data.address = item.name;
      },
      loadClicked() {
        console.log('Address:' + this.$data.address)
        if (!this.$data.address || '' == this.$data.address) {
          alert('No target address');
          return ;
        }

        const address = this.$data.address
        const alreadyExists = this.$data.addresses.find(address => address == address)
        if (!alreadyExists) {
          this.$data.addresses.push({ name: address });
        }
        this.$http.get('/contract/' + address).then((res) => {
          console.log('Response', res);
        });
        console.log('Request contract information for ' + address);
      },
      runClicked() {
        console.log('Run');
      }
    }
  }
</script>

<style></style>
