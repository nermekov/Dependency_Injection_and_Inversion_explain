package com.example.dv

import android.content.Context
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import com.example.dv.impl.*

/*
Метафора про провода и розетки
 */

/*
1. Не зависеть от имплементации. Принцип Dependency Inversion(картинка)
 */
interface SmsSubscription {
    fun unsubscribe()
}

//good
class Presenter {
    //Не знаем это GMS/HMS или другие подробности реализации
    var smsSubscription: SmsSubscription? = null
}

//bad
class Presenter2 {
    //Зависим от реализации напрямую
    //можно посмотреть, интересная реализация
    var smsSubscription: GmsSmsSubscription? = null
}

/*
2. Просить только интерфейсы в конструкторе.
 */
//bad
class Presenter3(smsSubscription: GmsSmsSubscription)

//good
class Presenter4(smsSubscription: SmsSubscription)

/*
3. DI помогает писать код с низкой связанностью(картинка)
DI простым языком - это получение зависимостей снаружи объекта
через конструктор или сеттеры
 */

/*
4. избегать "new", ожидать объекты через конструктор (Dependency Injection)
 */
//bad
class Presenter5(context: Context) {
    //похоже что для переменной удалось соболюсти inversion
    //но всё таки есть импорт реализации
    val smsSubscription: SmsSubscription? = GmsSmsSubscription(context, {})
}

//good
class Presenter6(
    context: Context,
    //Предоставить отдать объект DI
    private val smsSubscription: SmsSubscription
    //Либо Factory, если надо постоянно содавать новую подписку
    //SmsSubscriptionFactory { fun create(): SmsSubscription }
) {
}

/*
5. Побочное преимущество: начинать кодить раньше чем готова реализация интерфейсов
 */
//Представим что делаем экран и логику авторизации


//Но ещё не решили каким алгоритмом кодировать пароль
//Можем обойтись абстрактным Энкодером и быстро начинать работу,
//вне зависимости от Логики Encoder use case будет работать
interface PasswordEncoder {
    fun encodePassword(password: String): String
}

//Но ещё не решили где хранить данные, использовать ли кэш
//Можем обойтись абстрактным Repo и быстро начинать работу,
//от изменения источника данных use case не будет ломаться
interface LoginRepository {
    fun login(user: String, encodedPassword: String)
}

class LoginUseCase(
    passwordEncoder: PasswordEncoder,
    loginRepository: LoginRepository
) {
    fun doSomeLogic() {
        /**/
    }
}

/*
6. Разбор того что всё таки можно создавать через new, а что лучше передавать через DI
 */
//Через new можно Pojo/Dto
fun createUser() = UserPojo()

//Через new можно MutableLivedata/Flow
fun createMld() = MutableLiveData<String>()

//Системные объекты, которые требуют new: Bundle, Fragment
fun createBundle() = Bundle()

//Классы из библиотек, view, анимации и др инструменты, которые требуют new
fun createView(context: Context) = ConstraintLayout(context)

/*
Что рекомендуется через DI
 */
class MPresenter(
    //репозитории
    someRepository: SomeRepository,

    //юзкейсы и другая бизнес логика
    someUseCase: SomeUseCase,

    /* типы, которые требуют шейринга объектов
    например прослушивание события, что AppConfig загружен*/
    appConfigIsLoadedListener: AppConfigIsLoadedListener,

    //Отправка почт/смс итп должна быть завёрнута
    // в абстракции и тоже передаваться через di,
    smsSubscription: SmsSubscription
)

/*
Бонус: mini overview лекции Льва Екасова https://www.youtube.com/watch?v=TbEH1Upqrh
Что нам даёт Dependency Injection
- Внедрение зависимостей избавляет от копипасты создания сложного объекта с большим количеством аргументов
- Позволяет переиспользоваться объекты и зависимости:
 - экономия alloc - содание нового объекта в оперативке это дорого
 - позволяет использовать в репозитории кэш в переменных
- создаёт независимость от имплементации
- облегчает тестирование
 */
/*
DI Руками - проблемы:
- сложно создать большой граф зависимостей(Все виды data source(API, BD), repositories)
- сложно переиспользовать части графа зависисостей
- портянка кода создания зависимостей дублируется и в тестах
 */

/*
Источники
https://betterprogramming.pub/what-is-dependency-injection-b2671b1ea90a
https://betterprogramming.pub/five-principles-of-dependency-injection-5bd0cca9cb04
https://habr.com/ru/post/465395/
https://www.youtube.com/watch?v=TbEH1UpqrhM
* */