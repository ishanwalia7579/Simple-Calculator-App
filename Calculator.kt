import kotlin.math.*

// ANSI color codes for terminal colors
object Colors {
    const val RESET = "\u001B[0m"
    const val RED = "\u001B[31m"
    const val GREEN = "\u001B[32m"
    const val YELLOW = "\u001B[33m"
    const val BLUE = "\u001B[34m"
    const val PURPLE = "\u001B[35m"
    const val CYAN = "\u001B[36m"
    const val WHITE = "\u001B[37m"
    const val BOLD = "\u001B[1m"
}

fun printHeader(text: String) {
    println("\n${Colors.CYAN}${Colors.BOLD}╔════════════════════════════════════════════════════════════╗")
    println("║${text.padEnd(58)}║")
    println("╚════════════════════════════════════════════════════════════╝${Colors.RESET}")
}

fun printMenu(options: List<String>) {
    println("\n${Colors.YELLOW}${Colors.BOLD}┌──────────────────────────────────────────────────────────┐")
    options.forEachIndexed { index, option ->
        println("│ ${index + 1}. ${option.padEnd(52)} │")
    }
    println("└──────────────────────────────────────────────────────────┘${Colors.RESET}")
}

fun printResult(text: String) {
    println("\n${Colors.GREEN}${Colors.BOLD}► $text${Colors.RESET}")
}

fun printError(text: String) {
    println("\n${Colors.RED}${Colors.BOLD}⚠ $text${Colors.RESET}")
}

data class User(
    val username: String,
    val password: String,
    var cgpa: Double = 0.0,
    var calculationHistory: MutableList<String> = mutableListOf()
)

class UserManager {
    private val users = mutableMapOf<String, User>()
    
    fun register(username: String, password: String): Boolean {
        if (users.containsKey(username)) {
            return false
        }
        users[username] = User(username, password)
        return true
    }
    
    fun login(username: String, password: String): User? {
        return users[username]?.takeIf { it.password == password }
    }

    fun changePassword(username: String, oldPassword: String, newPassword: String): Boolean {
        val user = users[username]
        return if (user != null && user.password == oldPassword) {
            users[username] = user.copy(password = newPassword)
            true
        } else {
            false
        }
    }
}

class Calculator {
    private var currentValue: Double = 0.0
    private var lastOperation: String = ""
    private var newNumber: Boolean = true

    fun calculate(operation: String, number: Double): Double {
        when (operation) {
            "+" -> currentValue += number
            "-" -> currentValue -= number
            "*" -> currentValue *= number
            "/" -> if (number != 0.0) currentValue /= number else throw ArithmeticException("Cannot divide by zero")
            "%" -> currentValue = (currentValue * number) / 100
            "sqrt" -> currentValue = sqrt(number)
            "clear" -> {
                currentValue = 0.0
                lastOperation = ""
                newNumber = true
            }
            "sin" -> currentValue = sin(number * PI / 180)
            "cos" -> currentValue = cos(number * PI / 180)
            "tan" -> currentValue = tan(number * PI / 180)
            "log" -> currentValue = log10(number)
            "ln" -> currentValue = ln(number)
            "pow" -> currentValue = currentValue.pow(number)
            "fact" -> currentValue = factorial(number.toInt()).toDouble()
        }
        lastOperation = operation
        return currentValue
    }

    private fun factorial(n: Int): Long {
        return if (n <= 1) 1 else n * factorial(n - 1)
    }

    fun convertUnit(value: Double, fromUnit: String, toUnit: String): Double {
        return when {
            fromUnit == "km" && toUnit == "m" -> value * 1000
            fromUnit == "m" && toUnit == "km" -> value / 1000
            fromUnit == "kg" && toUnit == "g" -> value * 1000
            fromUnit == "g" && toUnit == "kg" -> value / 1000
            fromUnit == "c" && toUnit == "f" -> (value * 9/5) + 32
            fromUnit == "f" && toUnit == "c" -> (value - 32) * 5/9
            else -> throw IllegalArgumentException("Unsupported unit conversion")
        }
    }

    fun calculateCGPA(grades: List<Double>, credits: List<Int>): Double {
        if (grades.size != credits.size) {
            throw IllegalArgumentException("Number of grades must match number of credits")
        }
        var totalPoints = 0.0
        var totalCredits = 0
        for (i in grades.indices) {
            totalPoints += grades[i] * credits[i]
            totalCredits += credits[i]
        }
        return if (totalCredits > 0) totalPoints / totalCredits else 0.0
    }

    fun setValue(value: Double) {
        currentValue = value
    }

    fun getCurrentValue(): Double = currentValue
}

fun main() {
    val calculator = Calculator()
    val userManager = UserManager()
    var currentUser: User? = null
    
    while (true) {
        if (currentUser == null) {
            printHeader("Welcome to Enhanced Calculator")
            printMenu(listOf(
                "Login",
                "Register",
                "Exit"
            ))
            print("${Colors.CYAN}Choose an option: ${Colors.RESET}")
            
            when (readLine()) {
                "1" -> {
                    print("${Colors.YELLOW}Enter username: ${Colors.RESET}")
                    val username = readLine() ?: continue
                    print("${Colors.YELLOW}Enter password: ${Colors.RESET}")
                    val password = readLine() ?: continue
                    currentUser = userManager.login(username, password)
                    if (currentUser == null) {
                        printError("Invalid credentials!")
                    } else {
                        printResult("Welcome back, ${currentUser.username}!")
                    }
                }
                "2" -> {
                    print("${Colors.YELLOW}Enter new username: ${Colors.RESET}")
                    val username = readLine() ?: continue
                    print("${Colors.YELLOW}Enter new password: ${Colors.RESET}")
                    val password = readLine() ?: continue
                    if (userManager.register(username, password)) {
                        printResult("Registration successful!")
                    } else {
                        printError("Username already exists!")
                    }
                }
                "3" -> break
                else -> printError("Invalid option!")
            }
            continue
        }

        printHeader("Calculator Menu")
        printMenu(listOf(
            "Basic Calculator",
            "Scientific Calculator",
            "Unit Converter",
            "CGPA Calculator",
            "View My CGPA",
            "View Calculation History",
            "Change Password",
            "Logout",
            "Exit"
        ))
        print("${Colors.CYAN}Choose an option: ${Colors.RESET}")

        when (readLine()) {
            "1" -> {
                printHeader("Basic Calculator")
                println("${Colors.YELLOW}Available operations: +, -, *, /, %, sqrt, clear${Colors.RESET}")
                
                while (true) {
                    println("\n${Colors.BLUE}Current value: ${calculator.getCurrentValue()}${Colors.RESET}")
                    print("${Colors.YELLOW}Enter operation (or 'back' to return): ${Colors.RESET}")
                    val operation = readLine()?.lowercase() ?: break
                    
                    if (operation == "back") break
                    
                    if (operation == "clear") {
                        calculator.calculate("clear", 0.0)
                        printResult("Calculator cleared!")
                        continue
                    }
                    
                    if (operation == "sqrt") {
                        print("${Colors.YELLOW}Enter number: ${Colors.RESET}")
                        val number = readLine()?.toDoubleOrNull() ?: continue
                        try {
                            val result = calculator.calculate("sqrt", number)
                            currentUser.calculationHistory.add("√$number = $result")
                            printResult("Square root of $number = $result")
                        } catch (e: ArithmeticException) {
                            printError(e.message ?: "Error")
                        }
                        continue
                    }
                    
                    print("${Colors.YELLOW}Enter number: ${Colors.RESET}")
                    val number = readLine()?.toDoubleOrNull() ?: continue
                    
                    try {
                        val result = calculator.calculate(operation, number)
                        currentUser.calculationHistory.add("$operation $number = $result")
                        printResult("Result: $result")
                    } catch (e: ArithmeticException) {
                        printError(e.message ?: "Error")
                    }
                }
            }
            "2" -> {
                printHeader("Scientific Calculator")
                println("${Colors.YELLOW}Available operations: sin, cos, tan, log, ln, pow, fact${Colors.RESET}")
                
                while (true) {
                    println("\n${Colors.BLUE}Current value: ${calculator.getCurrentValue()}${Colors.RESET}")
                    print("${Colors.YELLOW}Enter operation (or 'back' to return): ${Colors.RESET}")
                    val operation = readLine()?.lowercase() ?: break
                    
                    if (operation == "back") break
                    
                    print("${Colors.YELLOW}Enter number: ${Colors.RESET}")
                    val number = readLine()?.toDoubleOrNull() ?: continue
                    
                    try {
                        val result = calculator.calculate(operation, number)
                        currentUser.calculationHistory.add("$operation($number) = $result")
                        printResult("Result: $result")
                    } catch (e: Exception) {
                        printError(e.message ?: "Error")
                    }
                }
            }
            "3" -> {
                printHeader("Unit Converter")
                printMenu(listOf(
                    "Kilometers to Meters (km to m)",
                    "Meters to Kilometers (m to km)",
                    "Kilograms to Grams (kg to g)",
                    "Grams to Kilograms (g to kg)",
                    "Celsius to Fahrenheit (c to f)",
                    "Fahrenheit to Celsius (f to c)"
                ))
                
                print("${Colors.YELLOW}Enter conversion type (1-6): ${Colors.RESET}")
                val type = readLine() ?: continue
                
                val (fromUnit, toUnit) = when (type) {
                    "1" -> Pair("km", "m")
                    "2" -> Pair("m", "km")
                    "3" -> Pair("kg", "g")
                    "4" -> Pair("g", "kg")
                    "5" -> Pair("c", "f")
                    "6" -> Pair("f", "c")
                    else -> {
                        printError("Invalid conversion type!")
                        continue
                    }
                }
                
                print("${Colors.YELLOW}Enter value to convert: ${Colors.RESET}")
                val value = readLine()?.toDoubleOrNull() ?: continue
                
                try {
                    val result = calculator.convertUnit(value, fromUnit, toUnit)
                    currentUser.calculationHistory.add("$value $fromUnit = $result $toUnit")
                    printResult("$value $fromUnit = $result $toUnit")
                } catch (e: IllegalArgumentException) {
                    printError(e.message ?: "Error")
                }
            }
            "4" -> {
                printHeader("CGPA Calculator")
                val grades = mutableListOf<Double>()
                val credits = mutableListOf<Int>()
                
                print("${Colors.YELLOW}Enter number of courses: ${Colors.RESET}")
                val numCourses = readLine()?.toIntOrNull() ?: continue
                
                for (i in 1..numCourses) {
                    print("${Colors.YELLOW}Enter grade for course $i (0-4): ${Colors.RESET}")
                    val grade = readLine()?.toDoubleOrNull() ?: continue
                    if (grade !in 0.0..4.0) {
                        printError("Invalid grade! Must be between 0 and 4")
                        continue
                    }
                    grades.add(grade)
                    
                    print("${Colors.YELLOW}Enter credits for course $i: ${Colors.RESET}")
                    val credit = readLine()?.toIntOrNull() ?: continue
                    if (credit <= 0) {
                        printError("Invalid credits! Must be positive")
                        continue
                    }
                    credits.add(credit)
                }
                
                try {
                    val cgpa = calculator.calculateCGPA(grades, credits)
                    currentUser.cgpa = cgpa
                    currentUser.calculationHistory.add("CGPA calculated: $cgpa")
                    printResult("Your CGPA is: %.2f".format(cgpa))
                } catch (e: IllegalArgumentException) {
                    printError(e.message ?: "Error")
                }
            }
            "5" -> {
                printHeader("Your CGPA")
                printResult("Current CGPA: %.2f".format(currentUser.cgpa))
            }
            "6" -> {
                printHeader("Calculation History")
                if (currentUser.calculationHistory.isEmpty()) {
                    printError("No calculations performed yet.")
                } else {
                    currentUser.calculationHistory.forEachIndexed { index, calc ->
                        println("${Colors.GREEN}${index + 1}. $calc${Colors.RESET}")
                    }
                }
            }
            "7" -> {
                printHeader("Change Password")
                print("${Colors.YELLOW}Enter current password: ${Colors.RESET}")
                val oldPassword = readLine() ?: ""
                print("${Colors.YELLOW}Enter new password: ${Colors.RESET}")
                val newPassword = readLine() ?: ""
                if (userManager.changePassword(currentUser.username, oldPassword, newPassword)) {
                    printResult("Password changed successfully!")
                } else {
                    printError("Current password incorrect!")
                }
            }
            "8" -> {
                currentUser = null
                printResult("Logged out successfully!")
            }
            "9" -> break
            else -> printError("Invalid option!")
        }
    }
}