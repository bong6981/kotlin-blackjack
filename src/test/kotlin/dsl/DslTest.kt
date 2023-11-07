package dsl

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * introduce {
 *   name("홍길동")
 *   company("활빈당")
 *   skills {
 *     soft("A passion for problem solving")
 *     soft("Good communication skills")
 *     hard("Kotlin")
 *   }
 *   languages {
 *     "Korean" level 5
 *     "English" level 3
 *   }
 * }
 */
class DslTest {
    @ValueSource(strings = ["박재성", "제이슨"])
    @ParameterizedTest
    fun name(name: String) {
        val person = introduce {
            name(name)
        }
        person.name shouldBe name
    }

    @Test
    fun company() {
        val person = introduce {
            name("박재성")
            company("우아한형제들")
        }
        person.name shouldBe "박재성"
        person.company shouldBe "우아한형제들"
    }

    @Test
    fun unemployed() {
        val person = introduce {
            name("박재성")
            company()
        }
        person.name shouldBe "박재성"
        person.company.shouldBeNull()
    }

    @Test
    fun skills() {
        val person = introduce {
            name("박재성")
            company()
            skills {
                soft("A passion for problem solving")
                soft("Good communication skills")
                hard("Kotlin")
            }
        }

        val skills = person.skills
        skills.shouldNotBeNull()
        skills.value shouldBe listOf(
            Skill(SkillType.SOFT, "A passion for problem solving"),
            Skill(SkillType.SOFT, "Good communication skills"),
            Skill(SkillType.HARD, "Kotlin")
        )
    }

    @Test
    fun languages() {
        val person = introduce {
            name("박재성")
            company()
            skills {
                soft("A passion for problem solving")
                soft("Good communication skills")
                hard("Kotlin")
            }
            languages {
                "Korean" level 5
                "English" level 3
            }
        }

        val languages = person.languages
        languages.shouldNotBeNull()
        languages.value shouldBe listOf(
            Language("Korean", 5),
            Language("English", 3),
        )
    }
}
