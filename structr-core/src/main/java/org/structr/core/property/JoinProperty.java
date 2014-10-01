/**
 * Copyright (C) 2010-2014 Morgner UG (haftungsbeschränkt)
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.core.property;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.neo4j.helpers.Predicate;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.GraphObject;
import org.structr.core.converter.PropertyConverter;

/**
 *
 * @author Christian Morgner
 */
public class JoinProperty extends StringProperty {

	private List<PropertyKey> keys = new ArrayList<>();

	public JoinProperty(final String name, final String separator, final PropertyKey... keys) {
		this(name, name, separator, keys);
	}

	public JoinProperty(final String jsonName, final String dbName, final String messageFormat, final PropertyKey... keys) {
		super(jsonName, dbName);

		this.format = messageFormat;
		this.keys.addAll(Arrays.asList(keys));

		this.passivelyIndexed();
	}

	@Override
	public String getProperty(SecurityContext securityContext, GraphObject obj, boolean applyConverter, final Predicate<GraphObject> predicate){

		final ArrayList<Object> arguments = new ArrayList<>();

		for (Iterator<PropertyKey> it = keys.iterator(); it.hasNext();) {

			final PropertyKey key                  = it.next();
			final PropertyConverter inputConverter = key.inputConverter(securityContext);

			if (inputConverter != null) {

				try {
					final Object value = inputConverter.revert(key.getProperty(securityContext, obj, applyConverter, predicate));
					if (value != null) {

						arguments.add(value);
					}

				} catch (FrameworkException fex) {
					fex.printStackTrace();
				}

			} else {

				final Object value = key.getProperty(securityContext, obj, applyConverter, predicate);
				if (value != null) {

					arguments.add(value);
				}
			}
		}

		try {
			return MessageFormat.format(format, arguments.toArray());

		} catch (Throwable t) { }

		return null;
	}

	@Override
	public void setProperty(SecurityContext securityContext, final GraphObject obj, String value) throws FrameworkException {

		final MessageFormat formatter = new MessageFormat(format, Locale.GERMAN);
		Object[] values                   = null;
		int len                           = 0;

		try {
			values = formatter.parse(value);
			len    = values.length;

		} catch (ParseException pex) {
			throw new FrameworkException(422, pex.getMessage());
		}

		for (int i=0; i<len; i++) {

			final PropertyKey key                  = keys.get(i);
			final PropertyConverter inputConverter = key.inputConverter(securityContext);

			if (inputConverter != null) {

				key.setProperty(securityContext, obj, inputConverter.convert(values[i]));

			} else {

				key.setProperty(securityContext, obj, values[i]);
			}
		}
	}
}