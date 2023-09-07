import java.util.concurrent.*
import java.time.*


def getRandomNumber(minRange, maxRange) {
    def random = new Random()
    random.nextInt(maxRange - minRange + 1) + minRange
}


/*
    Задание 1. Поиск максимального элемента массива
*/

def numberOfNumbers = 1000
def randomNumbers = []
numberOfNumbers.times {
    randomNumbers.push(getRandomNumber(1, 10000)) 
}


public int findMaxInList(list) {
    def max = list[0]
    for (number in list) {
        if (number > max) {
            max = number
        }
        Thread.sleep(1)
    }
    return max
}

/* Последовательно */
def sequentRun(list) {
    return findMaxInList(list)
}

// start = System.currentTimeMillis()
// println sequentRun(randomNumbers)
// println "${System.currentTimeMillis() - start}мс"


/* Многопоточность */
def multiThreadRun(list) {
    def numThreads = 4
    
    def subLists = list.collate(numThreads)
    
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads)
    
    def callables = subLists.collect { subList ->
        new Callable<Integer>() {
            @Override
            Integer call() {
                findMaxInList(subList)
            }
        }
    }

    def subListsMax = []
    try {
        subListsMax = executorService.invokeAll(callables).collect { future ->
            future.get()
        }
    } 
    finally {
        executorService.shutdown()
    }

    return findMaxInList(subListsMax)
}

// start = System.currentTimeMillis()
// println multiThreadRun(randomNumbers)
// println "${System.currentTimeMillis() - start}мс"


/* ForkJoin */

class MaxFinderTask extends RecursiveTask<Integer> {
    def list
    def function

    MaxFinderTask(list, function) {
        this.list = list
        this.function = function
    }

    @Override
    protected Integer compute() {
        def size = list.size()
        
        if (size <= 100) {
            return this.function(list)
        } else {
            def mid = size / 2
            
            def leftTask = new MaxFinderTask(list[0..mid], function)
            leftTask.fork()
            
            def rightTask = new MaxFinderTask(list[mid..size-1], function)

            def rightResult = rightTask.compute()
            def leftResult = leftTask.join()

            return Math.max(leftResult, rightResult)
        }
    }
}

def forkJoin(list) {
    def pool = new ForkJoinPool()
    return pool.invoke(new MaxFinderTask(list, this::findMaxInList))
}

// start = System.currentTimeMillis()
// println forkJoin(randomNumbers)
// println "${System.currentTimeMillis() - start}мс"

/*
    Задание 2. Ввод чисел
*/

def sleepRandomTime() {
    Thread.sleep(getRandomNumber(1, 5) * 1000)
}

def inputNumbers() {
    def lineReader = System.in.newReader()
    def endThread = false
    def futures = []

    def readThread = new Thread({
        while (true) {
            try {
                def number = Integer.valueOf(lineReader.readLine())
                def future = new FutureTask<>({ 
                    return number * number
                })

                futures << future
                future.run() 
            }
            catch (Exception e) {
                endThread = true
                break
            }
        }
    })

    def calculateThread = new Thread({
        while (!endThread || !futures.isEmpty()) {
            sleepRandomTime()
            if (futures.isEmpty()) {
                continue
            }
            
            def future = futures.remove(0)
            
            println future.get()
        }
    })

   readThread.start()
   calculateThread.start()

}

// inputNumbers()

/*
    Задание 3. Система обработки файлов
*/


class File {
    def fileType
    def fileSize

    File(fileType, fileSize) {
        this.fileType = fileType
        this.fileSize = fileSize
    }
}

class Generator implements Runnable {
    def queue
    def getRandomNumber

    Generator(queue, getRandomNumber) {
        this.queue = queue
        this.getRandomNumber = getRandomNumber
    }

    void run() {
        while (true) {
            try {
                def fileType = ['XML', 'JSON', 'XLS'][getRandomNumber(0, 2)]
                def fileSize = getRandomNumber(10, 100)
                def file = new File(fileType, fileSize)
                
                Thread.sleep(getRandomNumber(100, 1000))
                queue.put(file)
                
                println "Сгенерирован: ${fileType}, ${fileSize}"
                println "Файлов в очереди: ${queue.size()}"
            } catch (Exception e) {
                println e.message
                break
            }
        }
    }
}

class FileProcessor implements Runnable {
    def queue
    def supportedType

    FileProcessor(queue, supportedType) {
        this.queue = queue
        this.supportedType = supportedType
    }

    void run() {
        while (true) {
            try {
                def file = queue.take()
                if (file.fileType == supportedType) {
                    def processingTime = file.fileSize * 7
                    
                    Thread.sleep(processingTime)
                    
                    println "Обработан: ${supportedType} ${file.fileSize} ${processingTime}мс"
                }
            } catch (Exception e) {
                println e.message
                break
            }
        }
    }
}

def queue = new ArrayBlockingQueue<>(5)
def generator = new Thread(new Generator(queue, this::getRandomNumber))
def processorXML = new Thread(new FileProcessor(queue, 'XML'))
def processorJSON = new Thread(new FileProcessor(queue, 'JSON'))
def processorXLS = new Thread(new FileProcessor(queue, 'XLS'))


generator.start()
processorXML.start()
processorJSON.start()
processorXLS.start()
