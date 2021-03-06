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

import static org.springframework.data.gemfire.util.SpringUtils.defaultIfEmpty;

import java.lang.annotation.Annotation;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.util.TypeInformation;

/**
 * {@link PersistentEntity} implementation adding custom GemFire persistent entity related metadata, such as the
 * {@link com.gemstone.gemfire.cache.Region} to which the entity is mapped, etc.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @author Gregory Green
 * @see org.springframework.data.gemfire.mapping.GemfirePersistentProperty
 * @see org.springframework.data.mapping.model.BasicPersistentEntity
 */
@SuppressWarnings("unused")
public class GemfirePersistentEntity<T> extends BasicPersistentEntity<T, GemfirePersistentProperty> {

	private final Annotation regionAnnotation;

	private final String regionName;

	/* (non-Javadoc) */
	protected static Annotation resolveRegionAnnotation(Class<?> persistentEntityType) {

		for (Class<? extends Annotation> regionAnnotationType : Region.REGION_ANNOTATION_TYPES) {
			Annotation regionAnnotation = AnnotatedElementUtils.getMergedAnnotation(
				persistentEntityType, regionAnnotationType);

			if (regionAnnotation != null) {
				return regionAnnotation;
			}
		}

		return null;
	}

	/* (non-Javadoc) */
	protected static String resolveRegionName(Class<?> persistentEntityType, Annotation regionAnnotation) {

		String regionName = (regionAnnotation != null
			? getAnnotationAttributeStringValue(regionAnnotation, "value") : null);

		return defaultIfEmpty(regionName, persistentEntityType.getSimpleName());
	}

	/* (non-Javadoc) */
	protected static String getAnnotationAttributeStringValue(Annotation annotation, String attributeName) {
		return AnnotationAttributes.fromMap(AnnotationUtils.getAnnotationAttributes(annotation))
			.getString(attributeName);
	}

	/**
	 * Constructs a new instance of {@link GemfirePersistentEntity} initialized with the given {@link TypeInformation}
	 * describing the domain object (entity) {@link Class} type.
	 *
	 * @param information {@link TypeInformation} meta-data describing the domain object (entity) {@link Class} type.
	 * @throws IllegalArgumentException if the given {@link TypeInformation} is {@literal null}.
	 * @see org.springframework.data.util.TypeInformation
	 */
	public GemfirePersistentEntity(TypeInformation<T> information) {
		super(information);

		Class<T> rawType = information.getType();

		this.regionAnnotation = resolveRegionAnnotation(rawType);
		this.regionName = resolveRegionName(rawType, this.regionAnnotation);
	}

	/**
	 * Returns the {@link Region} {@link Annotation} used to annotate this {@link PersistentEntity} or {@literal null}
	 * if this {@link PersistentEntity} was not annotated with a {@link Region} {@link Annotation}.
	 *
	 * @param <T> concrete {@link Class} type of the {@link Region} {@link Annotation}.
	 * @return the {@link Region} {@link Annotation} used to annotate this {@link PersistentEntity} or {@literal null}
	 * if this {@link PersistentEntity} was not annotated with a {@link Region} {@link Annotation}.
	 * @see org.springframework.data.gemfire.mapping.Region
	 * @see java.lang.annotation.Annotation
	 */
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getRegionAnnotation() {
		return (T) this.regionAnnotation;
	}

	/**
	 * Returns the {@link Class} type of the {@link Region} {@link Annotation} used to annotate this entity
	 * or {@literal null} if this entity was not annotated with a {@link Region} {@link Annotation}.
	 *
	 * @return the {@link Class} type of the {@link Region} {@link Annotation} used to annotate this entity
	 * or {@literal null} if this entity was not annotated with a {@link Region} {@link Annotation}.
	 * @see java.lang.annotation.Annotation#annotationType()
	 * @see #getRegionAnnotation()
	 */
	public Class<? extends Annotation> getRegionAnnotationType() {
		Annotation regionAnnotation = getRegionAnnotation();
		return (regionAnnotation != null ? regionAnnotation.annotationType() : null);
	}

	/**
	 * Returns the name of the {@link com.gemstone.gemfire.cache.Region} in which this {@link PersistentEntity}
	 * will be stored.
	 *
	 * @return the name of the {@link com.gemstone.gemfire.cache.Region} in which this {@link PersistentEntity}
	 * will be stored.
	 * @see com.gemstone.gemfire.cache.Region#getName()
	 */
	public String getRegionName() {
		return this.regionName;
	}

	/**
	 * @inheritDoc
	 * @see org.springframework.data.mapping.model.BasicPersistentEntity#returnPropertyIfBetterIdPropertyCandidateOrNull(PersistentProperty)
	 */
	@Override
	protected GemfirePersistentProperty returnPropertyIfBetterIdPropertyCandidateOrNull(
			GemfirePersistentProperty property) {

		if (property.isIdProperty()) {
			if (hasIdProperty()) {
				GemfirePersistentProperty currentIdProperty = getIdProperty();

				if (currentIdProperty.isExplicitIdProperty()) {
					if (property.isExplicitIdProperty()) {
						throw new MappingException(String.format(
							"Attempt to add explicit id property [%1$s] but already have id property [%2$s] registered as explicit;"
								+ " Please check your object [%3$s] mapping configuration",
									property.getName(), currentIdProperty.getName(), getType().getName()));
					}

					return null;
				}

				return (property.isExplicitIdProperty() ? property : null);
			}
			else  {
				return property;
			}
		}

		return null;
	}
}
