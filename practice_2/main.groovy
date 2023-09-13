import java.io.*
import java.nio.*
import java.nio.channels.*
import java.nio.file.*

import org.apache.commons.io.*


folder = 'tracked folder'

def measureFunctionPerformance(action) {
    def startTime = System.currentTimeMillis()
    def runtime = Runtime.runtime
    runtime.gc()
    def memoryBefore = runtime.totalMemory() - runtime.freeMemory()
    
    action()
    
    runtime.gc()
    def memoryAfter = runtime.totalMemory() - runtime.freeMemory()
    def executionTime = System.currentTimeMillis() - startTime
    def memoryUsage = memoryAfter - memoryBefore
    
    println "${executionTime}мс ${memoryUsage / 1024}Mb"
}

/*
    Задание 1. Чтение файла
*/

def readLines(path) {
    def bufferedReader = new BufferedReader(new FileReader(new File(path)))
    bufferedReader.eachLine { line ->
        println line
    }
    bufferedReader.close()
}

// readLines("${folder}/read.txt")


/*
    Задание 2. Копирование файла
*/

def copyStream(path, newPath) {
    def in = new FileInputStream(path)
    def out = new FileOutputStream(newPath)
    
    def buffer = new byte[4096]

    def bytesNum
    while ((bytesNum = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesNum)
    }

    out.close()
    in.close()
}

def copyChannel(path, newPath) {
    def inChannel = FileChannel.open(Paths.get(path), StandardOpenOption.READ)
    def outChannel = FileChannel.open(Paths.get(newPath), StandardOpenOption.CREATE, StandardOpenOption.WRITE)

    inChannel.transferTo(0, inChannel.size(), outChannel)

    outChannel.close()
    inChannel.close()
}

def copyApache(path, newPath) {
    FileUtils.copyFile(new File(path), new File(newPath))
}

def copyFiles(path, newPath) {
    Files.copy(Paths.get(path), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING)
}


// measureFunctionPerformance { copyStream("${folder}/read.txt", "${folder}/copy_1.txt") }
// measureFunctionPerformance { copyChannel("${folder}/read.txt", "${folder}/copy_2.txt") } 
// measureFunctionPerformance { copyApache("${folder}/read.txt", "${folder}/copy_3.txt") }
// measureFunctionPerformance { copyFiles("${folder}/read.txt", "${folder}/copy_4.txt") }


/*
    Задание 3. 16 битная контрольная сумма
*/

def getChecksum(path) {
    def fileBytes = Files.readAllBytes(Paths.get(path))
    
    def buffer = ByteBuffer.wrap(fileBytes)
    
    def checksum = 0
    while (buffer.hasRemaining()) {
        checksum += buffer.get() & 0xFF
    }
    
    checksum &= 0xFFFF
    
    return checksum
}

// println getChecksum("${folder}/read.txt")
// println getChecksum("${folder}/copy_1.txt")


/*
    Задание 4. WatchService
*/

def getLines(path) {
    def lines = []
    def bufferedReader = new BufferedReader(new FileReader(new File(path)))
    bufferedReader.eachLine { line ->
        lines.push(line)
    }
    return lines
}

def compareLines(lines1, lines2) {
    lines1.eachWithIndex { line1, index ->
        def line2 = lines2[index]
        
        if (line1 != line2) {
            if (line2 != null) {
                println("Изменение: $line1 => $line2")
            } else {
                println("Удаление: $line1")
            }
        }
    }

    lines2.drop(lines1.size()).each { line ->
        println("Добавление: $line")
    }
}

def runWatchService(path) {
    
    def linesMap = [:]
    Files.list(Paths.get(path)).forEach { filePath ->
        def filename = filePath.getFileName().toString()
        linesMap[filename] = getLines(filePath.toString())
    }
    
    def watchService = FileSystems.getDefault().newWatchService()
    
    Paths.get(path).register(
        watchService, 
        StandardWatchEventKinds.ENTRY_CREATE, 
        StandardWatchEventKinds.ENTRY_DELETE, 
        StandardWatchEventKinds.ENTRY_MODIFY
    )

    def key
    while ((key = watchService.take()) != null) {
        key.pollEvents().each { event ->
            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                def filename = event.context()
                linesMap[filename] = getLines("${folder}/${filename}")
                println "Создан: ${filename}"
            } 
            else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                def filename = event.context().toString()
                
                def previousLines = linesMap[filename]
                def currentLines = getLines("${folder}/${filename}")
                
                println "Изменен: ${filename}"
                compareLines(previousLines, currentLines)
                linesMap[filename] = currentLines
            } 
            else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                def filename = event.context().toString()
                linesMap.remove(filename)
                
                println "Удален: ${filename}"
            }
        }
        key.reset()
    }
}

runWatchService(folder)
