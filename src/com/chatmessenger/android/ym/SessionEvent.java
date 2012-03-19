package com.chatmessenger.android.ym;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class SessionEvent {
	private final Map<String, Object> params = new HashMap<String, Object>();

	public void putParam(String key, Object value) {
		params.put(key, value);
	}

	public void putAll(Map<String, Object> map) {
		params.putAll(map);
	}

	public Object getParam(String key) {
	    return this.params.get(key);
	}
}
