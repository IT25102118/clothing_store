type ToastType = 'success' | 'error' | 'info';

interface ToastMessage {
  id: number;
  type: ToastType;
  message: string;
}

type Listener = (toasts: ToastMessage[]) => void;

let toasts: ToastMessage[] = [];
let nextId = 0;
let listeners: Listener[] = [];

function notify() {
  listeners.forEach((l) => l([...toasts]));
}

function remove(id: number) {
  toasts = toasts.filter((t) => t.id !== id);
  notify();
}

function add(type: ToastType, message: string) {
  const id = nextId++;
  toasts.push({ id, type, message });
  notify();
  setTimeout(() => remove(id), 4000);
}

export const toast = {
  success: (message: string) => add('success', message),
  error: (message: string) => add('error', message),
  info: (message: string) => add('info', message),
  subscribe: (listener: Listener) => {
    listeners.push(listener);
    return () => {
      listeners = listeners.filter((l) => l !== listener);
    };
  },
};
