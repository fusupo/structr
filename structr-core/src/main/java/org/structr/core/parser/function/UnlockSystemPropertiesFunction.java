/**
 * Copyright (C) 2010-2016 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.core.parser.function;

import org.structr.common.error.FrameworkException;
import org.structr.core.GraphObject;
import org.structr.core.entity.AbstractNode;
import org.structr.schema.action.ActionContext;
import org.structr.schema.action.Function;

/**
 *
 */
public class UnlockSystemPropertiesFunction extends Function<Object, Object> {

	public static final String ERROR_MESSAGE_UNLOCK_SYSTEM_PROPERTIES_ONCE    = "Usage: ${unlock_system_properties_once(node)}. Example ${unlock_system_properties_once, this}";
	public static final String ERROR_MESSAGE_UNLOCK_SYSTEM_PROPERTIES_ONCE_JS = "Usage: ${{Structr.unlock_system_properties_once(node)}}. Example ${{Structr.unlock_system_properties_once, Structr.get('this')}}";

	@Override
	public String getName() {
		return "unlock_system_properties_once()";
	}

	@Override
	public Object apply(final ActionContext ctx, final GraphObject entity, final Object[] sources) throws FrameworkException {

		if (arrayHasMinLengthAndAllElementsNotNull(sources, 1)) {

			if (sources[0] instanceof AbstractNode) {

				((AbstractNode)sources[0]).unlockSystemPropertiesOnce();

			} else {

				return usage(ctx.isJavaScriptContext());

			}
		}

		return "";
	}


	@Override
	public String usage(boolean inJavaScriptContext) {
		return (inJavaScriptContext ? ERROR_MESSAGE_UNLOCK_SYSTEM_PROPERTIES_ONCE_JS : ERROR_MESSAGE_UNLOCK_SYSTEM_PROPERTIES_ONCE);
	}

	@Override
	public String shortDescription() {
		return "Unlocks any system property for a single access";
	}

}