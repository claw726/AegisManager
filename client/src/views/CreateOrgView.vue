<template>
    <div>
        <NavBar />
        <div v-if="isLoggedIn">
            <h1>Create Org</h1>
        </div>
    </div>
</template>

<script>
import NavBar from '../components/NavBar.vue';
import { mapState } from 'vuex';

export default {
    components: {
        NavBar,
    },
    computed: {
      ...mapState(['isLoggedIn']),
    },
};
</script>
