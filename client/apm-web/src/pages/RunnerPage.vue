<template>
  <div class="row">
    <b-dropdown id="target-selector" :text="text" class="m-md-2">
      <b-dropdown-item v-for="item in targets" :key="item.name" @click="itemSelected(item)">{{item.name}}</b-dropdown-item>
    </b-dropdown>

    <b-input-group>
      <b-dropdown text="Select" variant="info" slot="prepend">
        <b-dropdown-item v-for="item in addresses" :key="item.name" @click="addressSelected(item)">{{item.name}}</b-dropdown-item>
      </b-dropdown>
      <b-form-input v-model="address" placeholder="Input your privatekey."></b-form-input>
    </b-input-group>

    <b-button @click="loadClicked">Load</b-button>
  </div>
</template>

<script>
  export default {
    props: ['targets'],
    data() {
      return {
        text: 'Select target...',
        address: '',
        addresses: []
      }
    },
    methods: {
      itemSelected(item) {
        console.log('Selected');
        this.$data.text = item.uuid;
      },
      addressSelected(item) {
        this.$data.address = item.name;
      },
      loadClicked() {
        if (this.$data.address || '' == this.$data.address) {
          alert('No target address');
          return ;
        }

        const address = this.$data.address
        const alreadyExists = this.$data.addresses.find(address => address.name == address)
        if (!alreadyExists) {
          this.$data.addresses.push({ name: address });
        }
        this.$http.get('/contract/' + address).then((res) => {
          console.log('Response', res);
        });
        console.log('Request contract information for ' + address);
      }
    }
  }
</script>

<style></style>
