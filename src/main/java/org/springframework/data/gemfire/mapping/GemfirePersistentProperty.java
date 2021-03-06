/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.gemfire.mapping;

import static org.springframework.data.gemfire.util.CollectionUtils.asSet;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.model.GemfireSimpleTypeHolder;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty;
import org.springframework.data.mapping.model.SimpleTypeHolder;

/**
 * {@link PersistentProperty} implementation to for Gemfire related metadata.
 *
 * @author Oliver Gierke
 */
public class GemfirePersistentProperty extends AnnotationBasedPersistentProperty<GemfirePersistentProperty> {

	protected static final Set<String> SUPPORTED_IDENTIFIER_NAMES = asSet("id");

	/* (non-Javadoc) */
	private static SimpleTypeHolder resolveSimpleTypeHolder(SimpleTypeHolder source) {
		return (source instanceof GemfireSimpleTypeHolder ? source
			: (source != null ? new GemfireSimpleTypeHolder(source) : new GemfireSimpleTypeHolder()));
	}

	/**
	 * Constructs an instance of the {@link GemfirePersistentProperty} initialized with entity
	 * persistent property information (meta-data).
	 *
	 * @param field entity {@link Field} corresponding to the persistent property.
	 * @param propertyDescriptor {@link PropertyDescriptor} for the entity's persistent property.
	 * @param owner entity owning the persistent property.
	 * @param simpleTypeHolder {@link SimpleTypeHolder} used to handle primitive types.
	 * @see org.springframework.data.mapping.model.SimpleTypeHolder
	 * @see org.springframework.data.mapping.PersistentEntity
	 * @see java.beans.PropertyDescriptor
	 * @see java.lang.reflect.Field
	 */
	public GemfirePersistentProperty(Field field, PropertyDescriptor propertyDescriptor,
			PersistentEntity<?, GemfirePersistentProperty> owner, SimpleTypeHolder simpleTypeHolder) {

		super(field, propertyDescriptor, owner, resolveSimpleTypeHolder(simpleTypeHolder));
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected Association<GemfirePersistentProperty> createAssociation() {
		return new Association<GemfirePersistentProperty>(this, null);
	}

	/**
	 * Determines whether this {@link GemfirePersistentProperty} explicitly identifies an entity property identifier,
	 * one in which the user explicitly annotated a entity class member (field or getter/setter).
	 *
	 * @return a boolean value indicating whether this {@link GemfirePersistentProperty} explicitly identifies
	 * an entity property identifier.
	 * @see org.springframework.data.annotation.Id
	 * @see #isAnnotationPresent(Class)
	 */
	public boolean isExplicitIdProperty() {
		return isAnnotationPresent(Id.class);
	}

	/**
	 * @inheritDoc
	 * @see org.springframework.data.mapping.model.AnnotationBasedPersistentProperty#isIdProperty()
	 */
	@Override
	public boolean isIdProperty() {
		return (super.isIdProperty() || SUPPORTED_IDENTIFIER_NAMES.contains(getName()));
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean usePropertyAccess() {
		return (super.usePropertyAccess() || (getField() == null && this.propertyDescriptor != null));
	}
}
