import java.util.*
import kotlin.random.Random

var numberOfMines = 0
var cellsWithHint = 0
lateinit var mark: Array<IntArray>
val dir = arrayOf(
    intArrayOf(1,0),
    intArrayOf(0,1),
    intArrayOf(0,-1),
    intArrayOf(-1,0),
    intArrayOf(1,1),
    intArrayOf(1,-1),
    intArrayOf(-1,1),
    intArrayOf(-1,-1)
)


fun main() {
    print("How many mines do you want on the field? ")
    val n = readLine()!!.toInt()
    numberOfMines = n
    mark = Array(9) { IntArray(9) }
    val field = prepareField(numberOfMines)
    printField(field)
    val result = playGame(field)
    if(result) {
        println("Congratulations! You found all the mines!")
    } else {
        println("You stepped on a mine and failed!")
    }
}

fun playGame(field : Array<CharArray>): Boolean {
    var mineMarked = 0
    var cellsWithHintMarked = 0
    while (mineMarked != numberOfMines && cellsWithHintMarked != cellsWithHint) {
        println("Set/unset mine marks or claim a cell as free: ")
        val(a, b, f) = readLine()!!.split(' ')
        var c = a.toInt()
        var r = b.toInt()
        c--
        r--
        val findMine = f == "mine"
        if (findMine) {
            if(mark[r][c] != 4) {
                mark[r][c] = 4
                if(field[r][c] == 'X') mineMarked++
            } else {
                mark[r][c] = 0
                if(field[r][c] == 'X') mineMarked--
            }
        } else {
            when (field[r][c]) {
                'X' -> {
                    for(i in 0..8) {
                        for(j in 0..8) {
                            if(field[i][j] == 'X') mark[i][j] = 3
                        }
                    }
                    printField(field)
                    return false
                }
                in '1'..'9' -> {
                    mark[r][c] = 2
                    cellsWithHintMarked++
                }
                '.' -> {
                    cellsWithHintMarked += exploreSurrounding(field,r,c)
                    println("$cellsWithHintMarked   $cellsWithHint")
                }
                else -> continue
            }
        }
        printField(field)
    }
    return true
}

fun exploreSurrounding(field: Array<CharArray>, r: Int, c: Int): Int {

    val q = LinkedList<Pair<Int,Int>>()
    val visited = Array(9){BooleanArray(9)}
    visited[r][c] = true
    mark[r][c] = 1
    q.add(Pair(r,c))
    var count = 0
    while (!q.isEmpty()) {
        val p = q.poll()
        val currRow = p.first
        val currCol = p.second
        for (d in dir) {
            val row = currRow + d[0]
            val col = currCol + d[1]
            if (row >= 0 && col >= 0 && row < field.size && col < field[0].size) {
                if (!visited[row][col]) {
                    if (field[row][col] == '.') {
                        q.add(Pair(row,col))
                        mark[row][col] = 1
                    } else {
                        count++
                        mark[row][col] = 2
                    }
                    visited[row][col] = true
                }
            }
        }
    }
    return count

}

fun prepareField(numberOfMines: Int): Array<CharArray> {
    val minesPerRow = numberOfMines / 9
    var extraMines = numberOfMines % 9
    val random = Random(100)
    val field = Array(9) { CharArray(9) { '.' } }
    for (r in 0..8) {
        var m = minesPerRow
        while (m > 0) {
            val index = random.nextInt(9)
            if (field[r][index] != 'X') {
                field[r][index] = 'X'
                m--
            }
        }
        if (extraMines > 0) {
            var index = random.nextInt(9)
            while (field[r][index] == 'X') {
                index = random.nextInt(9)
            }
            field[r][index] = 'X'
            extraMines--
        }
    }
    for (r in 0 until 9) {
        for (c in 0 until 9) {
            if (field[r][c] == 'X') updateHints(r, c, field)
        }
    }
    for (a in field) {
        println(a.concatToString())
    }
    return field
}

fun printField(field: Array<CharArray>) {

    println()
    println(" |123456789|")
    println("-|---------|")
    for (r in 0 until 9) {
        print("${r+1}|")
        for (c in 0 until 9) {
            when (mark[r][c]) {
                0 -> print('.')
                1 -> print('/')
                2 -> print(field[r][c])
                3 -> print('X')
                4 -> print('*')
            }
        }
        println('|')
    }
    println("-|---------|")

}

fun updateHints(row: Int, col: Int, arr: Array<CharArray>){

    for (d in dir) {
        val r = row + d[0]
        val c = col + d[1]
        if (r >= 0 && c >= 0 && r < 9 && c < 9){
            if (arr[r][c] != 'X'){
                if (arr[r][c] == '.') {
                    arr[r][c] = '1'
                    cellsWithHint++
                }
                else arr[r][c]++
            }
        }
    }

}

