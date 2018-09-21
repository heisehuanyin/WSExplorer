package ws.editor.comn;

public class WsPair<K, V> {
	private final K a;
	private final V b;
	
	public WsPair(final K a, final V b) {
		this.a = a;
		this.b = b;
	}
	public K getFirstElement() {
		return this.a;
	}
	public V getLastElement() {
		return this.b;
	}
}
