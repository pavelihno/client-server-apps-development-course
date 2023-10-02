import java.util.concurrent.*
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.*
import io.reactivex.rxjava3.schedulers.*


def getRandomNumber(minRange, maxRange) {
    def random = new Random()
    random.nextInt(maxRange - minRange + 1) + minRange
}

/*
    Задание 1
*/

class TemperatureSensor extends Observable<Integer> {

    def getRandomNumber
    
    TemperatureSensor(getRandomNumber) {
        this.getRandomNumber = getRandomNumber
    }

    @Override
    protected void subscribeActual(Observer<Integer> observer) {
        
        def disposable = new Disposable() {
            def disposed = false

            @Override
            public void dispose() { disposed = true }

            @Override
            public boolean isDisposed() { return disposed }
        }

        observer.onSubscribe(disposable)

        while (!disposable.isDisposed()) {
            def temperature = this.getRandomNumber(15, 30)
            observer.latestTemperature = temperature
            observer.onNext(temperature)
            try {
                Thread.sleep(1000)
            } catch (Exception e) {
                observer.onError(e)
                break
            }
        }

        observer.onComplete()
    }
}

class CO2Sensor extends Observable<Integer> {

    def getRandomNumber
    
    CO2Sensor(getRandomNumber) {
        this.getRandomNumber = getRandomNumber
    }

    @Override
    protected void subscribeActual(Observer<Integer> observer) {
        
        def disposable = new Disposable() {
            def disposed = false

            @Override
            public void dispose() { disposed = true }

            @Override
            public boolean isDisposed() { return disposed }
        }

        observer.onSubscribe(disposable)

        while (!disposable.isDisposed()) {
            def CO2 = this.getRandomNumber(30, 100)
            observer.latestCO2 = CO2
            observer.onNext(CO2)
            try {
                Thread.sleep(1000)
            } catch (Exception e) {
                observer.onError(e)
                break
            }
        }

        observer.onComplete()
    }
}

class Alarm implements Observer<Integer> {
    def latestTemperature = 0
    def latestCO2 = 0
    
    @Override
    void onSubscribe(Disposable d) {}

    @Override
    void onNext(Integer value) {
        println "Температура: ${latestTemperature}; СО2: ${latestCO2}"
        if (latestTemperature > 25 && latestCO2 <= 70) {
            println "Предупреждение: Температура превышает норму!"
        } else if (latestTemperature <= 25 && latestCO2 > 70) {
            println "Предупреждение: Уровень CO2 превышает норму!"
        } else if (latestTemperature > 25 && latestCO2 > 70) {
            println "ALARM!!!"
        }
    }

    @Override
    void onError(Throwable e) {}

    @Override
    void onComplete() {}
}

def runAlarm() {
    def temperatureSensor = new TemperatureSensor(this::getRandomNumber)
    def co2Sensor = new CO2Sensor(this::getRandomNumber)
    def alarm = new Alarm()

    def temperatureThread = new Thread({ temperatureSensor.subscribe(alarm) })
    def co2Thread = new Thread({ co2Sensor.subscribe(alarm) })

    temperatureThread.start()
    Thread.sleep(500)
    co2Thread.start()
}

// runAlarm()

/* 
    Задание 2.

    2.1.2
    2.2.2
    2.3.2    
*/
class CustomObserver<T> implements Observer<T> {
    @Override
    void onSubscribe(Disposable d) {}

    @Override
    void onNext(T result) {
        System.out.println(result)
    }

    @Override
    void onError(Throwable error) {
        System.out.println("Ошибка: ${error}")
    }

    @Override
    void onComplete() {
        System.out.println("Завершено")
    }
}

generateSequence = emitter -> {
    for (def i = 0; i < 10; i++) {
        emitter.onNext(getRandomNumber(0, 1000))
    }
    emitter.onComplete()
}

def task_1() {
    def randomNumbers = Observable.create(generateSequence)

    randomNumbers.filter { it > 500 }.subscribe(new CustomObserver())
}

def task_2() {
    def randomNumbers1 = Observable.create(generateSequence)
    def randomNumbers2 = Observable.create(generateSequence)

    Observable.concat(randomNumbers1, randomNumbers2).subscribe(new CustomObserver())
}

def task_3() {
    def randomNumbers = Observable.create(generateSequence)

    randomNumbers.take(5).subscribe(new CustomObserver())
}

// task_1()
// task_2()
// task_3()

/*
    Задание 3
*/

class UserFriend {
    def userId
    def friendId

    UserFriend(userId, friendId) {
        this.userId = userId
        this.friendId = friendId
    }
}

def getFriends(user) {
    return Observable.create { emitter ->
        def friends = (1..5).collect { getRandomNumber(0, 100) }
        friends.each { friendId ->
            emitter.onNext(new UserFriend(user, friendId))
        }
        emitter.onComplete()
    }
}

def runUserFriend() {
    
    def userObservable = Observable.create { emitter ->
        def users = (1..3).collect { getRandomNumber(0, 100) }
        users.each { user ->
            emitter.onNext(user)
        }
        emitter.onComplete()
    }

    def userFriendObservable = userObservable.flatMap { user -> getFriends(user) }

    userFriendObservable.subscribe(
        { userFriend -> println "userId: ${userFriend.userId}, friendId: ${userFriend.friendId}" },
        { error -> println "Ошибка: ${error}" },
        { println "Завершено" }
    )
}

// runUserFriend()


/*
    Задание 4
*/


class File {
    def fileType
    def fileSize

    File(fileType, fileSize) {
        this.fileType = fileType
        this.fileSize = fileSize
    }
}

class Generator {
    def queue
    def getRandomNumber

    Generator(queue, getRandomNumber) {
        this.queue = queue
        this.getRandomNumber = getRandomNumber
    }

    def generate() {
        return Observable.create(emitter -> {
            while (!emitter.isDisposed()) {
                def fileType = ['XML', 'JSON', 'XLS'][getRandomNumber(0, 2)]
                def fileSize = getRandomNumber(10, 100)
                def file = new File(fileType, fileSize)
                queue.add(file)

                System.out.println("Сгенерирован: " + fileType + ", " + fileSize)
                System.out.println("Файлов в очереди: " + queue.size())

                emitter.onNext(file)

                try {
                    Thread.sleep(getRandomNumber(100, 1000))
                } catch (InterruptedException e) {
                    emitter.onError(e)
                    break
                }
            }
            emitter.onComplete()
        }).subscribeOn(Schedulers.io())
    }
}

class FileProcessor {
    def queue
    def supportedType

    FileProcessor(queue, supportedType) {
        this.queue = queue
        this.supportedType = supportedType
    }

    def process() {
        return Observable.create(emitter -> {
            while (!emitter.isDisposed()) {
                try {
                    def file = queue.take()
                    if (file.fileType.equals(supportedType)) {
                        int processingTime = file.fileSize * 7
                        Thread.sleep(processingTime)
                        emitter.onNext("Обработан: ${supportedType}, ${file.fileSize}, ${processingTime}мс")
                    }
                } catch (InterruptedException e) {
                    emitter.onError(e)
                    break
                }
            }
        }).observeOn(Schedulers.io())
    }
}

onNextAction = { result -> System.out.println(result) }
onErrorAction = { error -> println "Ошибка: ${error}" }
onCompleteAction = {}

def processFiles() {
    def queue = new ArrayBlockingQueue<>(5)
    def generator = new Generator(queue, this::getRandomNumber)

    generator.generate().subscribe()

    def processorXML = new FileProcessor(queue, 'XML')
    def processorJSON = new FileProcessor(queue, 'JSON')
    def processorXLS = new FileProcessor(queue, 'XLS')

    processorXML.process().subscribe(onNextAction, onErrorAction, onCompleteAction)
    processorJSON.process().subscribe(onNextAction, onErrorAction, onCompleteAction)
    processorXLS.process().subscribe(onNextAction, onErrorAction, onCompleteAction)
}

// processFiles()