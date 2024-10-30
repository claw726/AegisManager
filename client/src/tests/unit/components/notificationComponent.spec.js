
// notificationComponent.spec.js
import { mount } from '@vue/test-utils';
import NotificationComponent from '@/components/NotificationComponent.vue';
import { beforeEach, describe, it, expect, vi } from "vitest";

describe('NotificationComponent', () => {
  it('returns the correct notification styles', () => {
    const wrapper = mount(NotificationComponent, {
      propsData: {
        type: 'success',
      },
    });
    expect(wrapper.vm.notificationStyles).toBe('bg-green-50 text-green-700 border border-green-200');
  });

  it('returns the correct close button styles', () => {
    const wrapper = mount(NotificationComponent, {
      propsData: {
        type: 'success',
      },
    });
    expect(wrapper.vm.closeButtonStyles).toBe('hover:bg-green-100');
  });
  it('renders the notification message', () => {
    const wrapper = mount(NotificationComponent, {
      propsData: {
        show: true,
        type: 'success',
      },
      slots: {
        default: 'Test message',
      },
    });
    expect(wrapper.text()).toContain('Test message');
  });

  it('renders the close button', () => {
    const wrapper = mount(NotificationComponent, {
      propsData: {
        show: true,
        type: 'success',
      },
    });
    expect(wrapper.find('button').exists()).toBe(true);
  });
});