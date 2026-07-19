import { useEffect, useState } from 'react';
import { toast } from '../../utils/toast';
import { CheckCircle, XCircle, Info, X } from 'lucide-react';

interface ToastMessage {
  id: number;
  type: 'success' | 'error' | 'info';
  message: string;
}

export function ToastContainer() {
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  useEffect(() => {
    const unsub = toast.subscribe(setToasts);
    return unsub;
  }, []);

  if (toasts.length === 0) return null;

  const icons = {
    success: <CheckCircle className="h-5 w-5 text-green-500" />,
    error: <XCircle className="h-5 w-5 text-red-500" />,
    info: <Info className="h-5 w-5 text-blue-500" />,
  };

  const bgColors = {
    success: 'bg-green-50 border-green-200',
    error: 'bg-red-50 border-red-200',
    info: 'bg-blue-50 border-blue-200',
  };

  return (
    <div className="fixed top-4 right-4 z-[100] flex flex-col gap-2 max-w-sm">
      {toasts.map((t) => (
        <div key={t.id} className={`flex items-start gap-3 px-4 py-3 rounded-lg border shadow-lg ${bgColors[t.type]} animate-slide-in`}>
          {icons[t.type]}
          <p className="text-sm flex-1">{t.message}</p>
          <button
            onClick={() => {
              /* toast removal handled by timeout */
            }}
            className="text-gray-400 hover:text-gray-600"
          >
            <X className="h-4 w-4" />
          </button>
        </div>
      ))}
    </div>
  );
}
