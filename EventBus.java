public class EventBus {
    private static volatile EventBus instance;

    private Map<String, Set<Action1> > subscriberMap = new HashMap<>();
    private Map<String, Set<Action1> > onceSubscriberMap = new HashMap<>();

    public static EventBus getInstance() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    /**
     * 注册事件订阅器
     * @param event 事件名称
     * @param subscriber 订阅者
     */
    public synchronized <T, W extends Event<T>> void register(String event, Action1<W> subscriber) {
        Set<Action1> subscribers = subscriberMap.get(event);
        if (subscribers == null) {
            subscribers = new HashSet<>();
            subscriberMap.put(event, subscribers);
        }
        subscribers.add(subscriber);
    }

    /**
     * 注册事件订阅器，接收一次之后自动解除订阅
     * @param event 事件名称
     * @param subscriber 订阅者
     */
    public synchronized <T, W extends Event<T>> void registerOnce(String event, Action1<W> subscriber) {
        Set<Action1> subscribers = onceSubscriberMap.get(event);
        if (subscribers == null) {
            subscribers = new HashSet<>();
            onceSubscriberMap.put(event, subscribers);
        }
        subscribers.add(subscriber);
    }

    /**
     * 取消事件订阅
     * @param event 事件名称
     * @param subscriber 订阅者
     */
    public synchronized <T, W extends Event<T>>  void unregister(String event, Action1<W> subscriber) {
        Set<Action1> subscribers = subscriberMap.get(event);
        if (subscribers != null) {
            if (subscriber != null) {
                subscribers.remove(subscriber);
            }
            else {
                subscribers.clear();
            }
        }
    }

    /**
     * 发送空事件
     * @param key 事件名称
     */
    public void post(String key) {
        post(key, null) ;
    }

    /**
     * 发送事件
     * @param key 事件名称
     * @param event 事件数据
     */
    @SuppressWarnings("unchecked")
    public <T> void post(String key, final Event<T> event) {
        List<Action1> subscribers = new ArrayList<>();
        synchronized (this) {
            Set<Action1> list = subscriberMap.get(key);
            if (list != null) {
                subscribers.addAll(list);
            }

            list = onceSubscriberMap.remove(key);
            if (list != null) {
                subscribers.addAll(list);
            }
        }

        for (final Action1<Event> subscriber : subscribers) {
            try {
                subscriber.call(event);
            }
            catch (Exception e) {
                LogManager.e("EventBus", "subscriber call exception: %s", e);
            }
        }
    }
}
