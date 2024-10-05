<template>
    <div class="menu-item" @click="isOpen = !isOpen">
        <div class="relative mr-48 mt-24 critical-button">
            <div class="text-gray-800">⚙️</div>
        </div>
        <transition name="fade" appear>
            <div class="sub-menu bg-secondary p-2 mr-2 rounded-xl drop-shadow-sm max-w-max" v-if="isOpen">
            <div  @click="item.command()" v-for="(item, i) in items" :key="i" class="menu-item bg-white border p-1 m-1 rounded-lg drop-shadow text-lg dashboard-button">
                {{ item.title }}
            </div>
        </div>
        </transition>
        
    </div>
</template>

<script>
    export default {
        name: 'Dropdown',
        props: ['title', 'items'],
        methods: {
            executeCommand() {
                this.$emit('command');
            },
        },
        data() {
            return {
                isOpen: false
            }
        },
    }
</script>

<style>

.menu_item svg {
    width: 10px;
    margin-left: 10px;
}

.menu_item .sub-menu {
    position: absolute;
    background-color: #222;
    top: calc(100% + 18px);
    left: 50%;
    transform: translateX(-50%);
    width: max-content;
    border-radius: 0px 0px 16px 16px;
    z-index: 100;
}

.fade-enter-active{
    transition: all .5s ease-in-out;
}
.fade-leave-active {
    transition: all .5s ease-in-out;
}

.fade-enter {
    opacity: 0;
}

.fade-leave-to {
    opacity: 0;
}

</style>