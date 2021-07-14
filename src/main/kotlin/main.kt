import java.text.NumberFormat
import kotlin.math.min
import kotlin.math.roundToInt

val rubWordForm = arrayOf("рубль", "рубля", "рублей")
val pennyWordForm = arrayOf("копейка", "копейки", "копеек")

fun main() {
    var prevSum: Int = 0
    // from 3rd order additional discount
    var isOldfag = false
    var orderNum = 1
    while (true) {
        try {
            println("Введите сумма заказа №$orderNum, exit для выхода")
            val line = readLine()
            if (line == "exit") {
                break
            }
            val sum = line!!.toInt() * 100
            if (sum < 0) {
                throw Exception("Must be greater then 0")
            }
            if (orderNum >= 3) {
                isOldfag = true
            }
            println(getFormattedSumWithDiscount(sum, prevSum, isOldfag))
            prevSum = sum
            orderNum++
        } catch (e: Exception) {
            println("Введено некоректное значение")
        }
    }
}

fun getFormattedSumWithDiscount(sum: Int, prevSum: Int, isOldfag: Boolean): String {
    var finalSum: Int
    val builder = StringBuilder()

    val lowDiscountPerc = 5;

    val midDiscountStarts = 100100
    val midDiscountEnds = 1000000
    val midDiscountSum = 10000

    val highDiscountPerc = 5;
    val oldfagDiscountPerc = 1;
    builder.append("Покупка ${getSumString(sum)}")
    if (prevSum in midDiscountStarts..midDiscountEnds) {
        finalSum = sum - midDiscountSum
        builder.append("  -> \nПосле применения скидки ${getSumString(midDiscountSum)} - ${getSumString(finalSum)}")
    } else if (prevSum > midDiscountEnds) {
        finalSum = (sum * (100 - highDiscountPerc.toDouble()) / 100).roundToInt()
        builder.append("  -> \nПосле применения скидки $highDiscountPerc% - ${getSumString(finalSum)}")
    } else {
        finalSum = sum
    }

    if (isOldfag) {
        (finalSum * 0.99).roundToInt().also { finalSum = it }
        builder.append("  -> \nПосле применения скидки $oldfagDiscountPerc% - ${getSumString(finalSum)}\n")
    }

    return builder.toString()
}

fun getPluralForm(count: Int, words: Array<String>): String {
    val cases = intArrayOf(2, 0, 1, 1, 1, 2)
    val word = words[if (count % 100 in 5..19) 2 else cases[min(count % 10, 5)]]

    return word
}

fun getSumString(sum: Int): String {
    val rub = sum / 100
    val penny = sum % 100

    val rubWord = getPluralForm(rub, rubWordForm)
    val pennyWord = getPluralForm(penny, pennyWordForm)

    val builder = StringBuilder();
    if (rub > 0) {
        builder.append("${NumberFormat.getNumberInstance().format(rub)} $rubWord");
    }
    if (penny > 0) {
        builder.append(" ")
        if (rub > 0) {
            builder.append(penny.toString().padStart(2, '0'))
        } else {
            builder.append(penny)
        }
        builder.append(" $pennyWord")
    }
    return builder.toString();
}