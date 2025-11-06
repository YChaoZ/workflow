import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    collapse: false,
    device: 'desktop'
  }),
  actions: {
    toggleCollapse() {
      this.collapse = !this.collapse
    },
    setDevice(device: string) {
      this.device = device
    }
  }
})

