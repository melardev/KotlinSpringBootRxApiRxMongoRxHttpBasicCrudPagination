package com.melardev.spring.rest.seeds

import com.github.javafaker.Faker
import com.melardev.spring.rest.entities.Role
import com.melardev.spring.rest.entities.Todo
import com.melardev.spring.rest.entities.User
import com.melardev.spring.rest.repositories.TodosRepository
import com.melardev.spring.rest.services.AppUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import java.util.stream.LongStream


@Service
class DbSeeder : CommandLineRunner {

    @Autowired
    internal lateinit var todosRepository: TodosRepository

    @Autowired
    internal lateinit var userService: AppUserDetailsService

    override fun run(vararg args: String) {
        // todosRepository.deleteAll().block()

        val todosCount = this.todosRepository.count().block()
        val faker = Faker(Random(System.currentTimeMillis()))
        var todosToSeed: Long = 18

        if (todosCount != null)
            todosToSeed -= todosCount

        val startDate: Date = Date.from(LocalDateTime.of(2016, 1, 1, 0, 0).toInstant(ZoneOffset.UTC))
        val endDate: Date = Date.from(LocalDateTime.of(2019, 1, 1, 0, 0).toInstant(ZoneOffset.UTC))

        val todos = LongStream.range(0, todosToSeed).mapToObj { _ ->
            val todo = Todo(
                    StringUtils.collectionToDelimitedString(faker.lorem().words(faker.random().nextInt(2, 5)), "\n"),
                    org.apache.commons.lang3.StringUtils.join(faker.lorem().sentences(faker.random().nextInt(1, 3)), "\n"),
                    faker.random().nextBoolean())

            val dateForCreatedAt = faker.date().between(startDate, endDate)
            val createdAt = dateForCreatedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            val dateForUpdatedAt = faker.date().future(2 * 365, TimeUnit.DAYS, dateForCreatedAt)
            val updatedAt = dateForUpdatedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

            todo.createdAt = createdAt
            todo.updatedAt = updatedAt
            todo
        }.collect(Collectors.toSet())

        todosRepository.saveAll(todos).subscribe()

        println("[+] Seeded $todosToSeed todos")

        // val currentUsersInDb = this.userService.countSync()
        this.userService.deleteAll()
                .thenMany<Any> {
                    val admin = User()
                    admin.username = "admin"
                    admin.password = "admin"
                    admin.addRole(Role("ROLE_ADMIN"))

                    val user = User()
                    user.username = "user"
                    user.password = "user"
                    user.addRole(Role("ROLE_USER"))

                    userService.saveAll(admin, user).subscribe()
                }.subscribe()
    }
}
