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
 *
 */

package org.springframework.data.gemfire.test.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.gemfire.mapping.Region;
import org.springframework.data.gemfire.test.support.IdentifierSequence;
import org.springframework.data.gemfire.util.SpringUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * The Person class...
 *
 * @author John Blum
 * @since 1.0.0
 */
@Region("People")
@SuppressWarnings("unused")
public class Person implements Serializable {

	protected static final String BIRTH_DATE_PATTERN = "yyyy/MM/dd";

	private Date birthDate;

	private Gender gender;

	@Id
	private Long id;

	private final String firstName;
	private final String lastName;

	public static Date newBirthDate(int year, int month, int dayOfMonth) {
		Calendar birthDate = Calendar.getInstance();
		birthDate.clear();
		birthDate.set(Calendar.YEAR, year);
		birthDate.set(Calendar.MONTH, month);
		birthDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		return birthDate.getTime();
	}

	public static Person newPerson(String firstName, String lastName, Date birthDate, Gender gender) {
		return newPerson(IdentifierSequence.nextId(), firstName, lastName, birthDate, gender);
	}

	public static Person newPerson(Long id, String firstName, String lastName, Date birthDate, Gender gender) {
		return new Person(id, firstName, lastName, birthDate, gender);
	}

	@PersistenceConstructor
	public Person(String firstName, String lastName, Date birthDate, Gender gender) {
		Assert.hasText(firstName, "firstName must be specified");
		Assert.hasText(lastName, "lastName must be specified");

		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = (birthDate != null ? (Date) birthDate.clone() : null);
		this.gender = gender;
	}

	public Person(Long id, String firstName, String lastName, Date birthDate, Gender gender) {
		this(firstName, lastName, birthDate, gender);
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = (birthDate != null ? (Date) birthDate.clone() : null);
	}

	public String getFirstName() {
		return firstName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getLastName() {
		return lastName;
	}

	public String getName() {
		return String.format("%1$s %2$s", getFirstName(), getLastName());
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof Person)) {
			return false;
		}

		Person that = (Person) obj;

		return SpringUtils.equalsIgnoreNull(this.getId(), that.getId())
			&& (ObjectUtils.nullSafeEquals(this.getBirthDate(), that.getBirthDate()))
			&& (ObjectUtils.nullSafeEquals(this.getFirstName(), that.getFirstName())
			&& (ObjectUtils.nullSafeEquals(this.getGender(), that.getGender()))
			&& (ObjectUtils.nullSafeEquals(this.getLastName(), that.getLastName())));
	}

	@Override
	public int hashCode() {
		int hashValue = 17;
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getId());
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getBirthDate());
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getFirstName());
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getLastName());
		return hashValue;
	}

	protected static String toString(Date dateTime, String DATE_FORMAT_PATTERN) {
		return (dateTime == null ? null : new SimpleDateFormat(DATE_FORMAT_PATTERN).format(dateTime));
	}

	@Override
	public String toString() {
		return String.format(
			"{ @type = %1$s, id = %2$d, firstName = %3$s, lastName = %4$s, birthDate = %5$s, gender = %6$s}",
				getClass().getName(), getId(), getFirstName(), getLastName(),
					toString(getBirthDate(), BIRTH_DATE_PATTERN), getGender());
	}
}
