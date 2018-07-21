package de.schauderhaft.instantmax;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@DataJpaTest
public class InstantMaxApplicationTests {

	static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			"DD.MM.YYY"
	);
	static final Instant WAR = toInstant("28.7.1914");
	static final Instant FESTIVAL = toInstant("15.8.1969");
	static final Instant WALL = toInstant("9.11.1989");
	static final Instant DAY_AFTER = toInstant("21.1.2017");
	static final Instant CUSTOM_MIN = new Date(Long.MIN_VALUE / 2).toInstant();
	static final Instant CUSTOM_MAX = new Date(Long.MAX_VALUE / 2).toInstant();
	@Autowired
	PersonRepository repository;

	@Before
	public void before() {
		repository.save(createPerson("Pa - Pa", "1.9.1928"));
		repository.save(createPerson("Pa", "15.10.1970"));
		repository.save(createPerson("Son", "23.8.2003"));
	}

	private Person createPerson(String name, String dobString) {
		Person grandpa = new Person();
		grandpa.setName(name);
		grandpa.setDob(toInstant(dobString));
		return grandpa;
	}

	private static Instant toInstant(String dateString) {
		try {
			return FORMAT.parse(dateString).toInstant();
		} catch (ParseException e) {
			throw new IllegalArgumentException("couldn't parse date.", e);
		}
	}

	@Test
	public void findOneByDateRange() {
		assertThat(repository.findByDobBetween(FESTIVAL, WALL)).extracting(Person::getName).containsExactlyInAnyOrder("Pa");
	}

	@Test
	public void findAllByDateRange() {
		assertThat(repository.findByDobBetween(WAR, DAY_AFTER)).extracting(Person::getName).containsExactlyInAnyOrder("Pa - Pa", "Pa", "Son");
	}

	@Test
	public void findAllByMinMax() {
		assertThat(repository.findByDobBetween(Instant.MIN, Instant.MAX)).extracting(Person::getName).containsExactlyInAnyOrder("Pa - Pa", "Pa", "Son");
	}

	@Test
	public void findAllByCustomMinMax() {
		assertThat(repository.findByDobBetween(CUSTOM_MIN, CUSTOM_MAX)).extracting(Person::getName).containsExactlyInAnyOrder("Pa - Pa", "Pa", "Son");
	}

}
