package org.insightech.er.editor.model.settings;

import java.io.Serializable;

public class Environment implements Serializable, Cloneable {

	private static final long serialVersionUID = 2894497911334351672L;

	private String name;

	public Environment(String name) {
		this.name = name;
	}

	/**
	 * name を取得します.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * name を設定します.
	 * 
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Environment clone() {
		try {
			Environment environment = (Environment) super.clone();

			return environment;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
