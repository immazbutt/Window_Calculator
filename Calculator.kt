import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import javafx.stage.Stage
import tornadofx.*
import java.lang.Math.*
import javax.script.ScriptEngineManager
import javax.swing.plaf.nimbus.State
import kotlin.math.pow


class CalculatorApp : App() {
    override val primaryView = Calculator::class
    override fun start(stage: Stage) {
        importStylesheet("/style.css")
        stage.isResizable = false
        super.start(stage)
    }
}

class Calculator : View() {
    override val root: VBox by fxml()

    @FXML
    lateinit var display: Label

    @FXML
    lateinit var memory: Label
    var Num1: Double? = null;
    var Num2: Double? = null;
    var Operator: String? = null;
    var Statement: String = ""
    var Braces: Boolean = false;

    init {
        title = "Calculator"
        root.lookupAll(".button").forEach { button ->
            button.setOnMouseClicked {
                Operation((button as Button).text)
            }
        }

        // Keyboard support
        root.addEventFilter(KeyEvent.KEY_TYPED) {
            Operation(it.character.toUpperCase().replace("\r", "="))
        }
    }

    fun Operation(N: String) {
        try {
            if (N.equals("C"))
                Clr()
            else if (N.equals("CE")) {
                Clr()
                memory.text = ""
            } else {
                if (!Braces) {
                    if (N.equals("(")) {
                        display.text = "("
                        Braces = true;
                    } else if (N.equals("bk")) {
                        if (display.text.length > 0)
                            display.text = display.text.substring(0, display.text.length - 1)
                    } else if (N.equals("*") || N.equals("+") || N.equals("-") || N.equals("/") || N.equals("%")) {
                        if (!display.text.isNullOrEmpty()) {
                            if (Num1 == null) {
                                Num1 = display.text.toDouble()
                                Statement += Num1
                            } else {
                                Num2 = display.text.toDouble()
                                Statement = Statement + Operator + Num2;
                            }
                        }
                        Operator = N
                        display.text = ""
                    }
                    else if(N.equals("mod") || N.equals("exp") || N.equals("^")){
                        if (!display.text.isNullOrEmpty()) {
                            if (Num1 == null) {
                                Num1 = display.text.toDouble()
                                display.text = ""
                            } else {
                                Num2 = display.text.toDouble()
                            }
                            if (Num1 != null && Num2 != null && Operator != null) {
                                op(Operator!!)
                                Statement = Num1.toString()
                            }
                        }
                        Operator = N
                        display.text = ""
                    }
                    else if (N.equals("sin") || N.equals("cos") || N.equals("tan") || N.equals("√") || N.equals("log") || N.equals("ln") || N.equals("e^") || N.equals("10^") || N.equals("π")) {
                        Operator = N
                        if (!display.text.isNullOrEmpty()) {
                            Num1 = display.text.substring(0, display.text.length).toDouble()
                            if (Num1 != null && Operator != null)
                                op(Operator!!)
                        }
                        display.text = ""
                    } else if (N.equals("+/-")) {
                        display.text = (display.text.toDouble() * -1).toString()
                    } else if (N.equals("=")) {
                        if(Statement != "") {
                            Statement = Statement + Operator + display.text.toDouble()
                            print(Statement)
                            val scriptEngineManager = ScriptEngineManager()
                            val scriptEngine = scriptEngineManager.getEngineByName("JavaScript")
                            val ob = scriptEngine.eval(Statement)
                            display.text = ob.toString()
                            memory.text = Statement
                            Statement = ""
                            Num1 = display.text.toDouble()
                            Operator = null
                        }
                        else{
                            Num2 = display.text.toDouble()
                            if (Operator != null)
                                op(Operator!!)
                            display.text = ""
                            memory.text = Num1.toString()
                        }
                    } else {
                        display.text += N
                    }
                } else {
                    display.text += N
                    if (N.equals(")")) {
                        memory.text = display.text
                        val scriptEngineManager = ScriptEngineManager()
                        val scriptEngine = scriptEngineManager.getEngineByName("JavaScript")
                        val ob = scriptEngine.eval(display.text)
                        display.text = ob.toString();
                        Braces = false;
                    }
                }
            }
        } catch (e: Exception) {
            print("Bad Syntax")
        }
    }

    fun op(x: String): Unit {
        when (x) {
            "+" -> Add(Num1, Num2)
            "-" -> Sub(Num1, Num2)
            "/" -> Div(Num1, Num2)
            "*" -> Mul(Num1, Num2)
            "^" -> Pow(Num1, Num2)
            "%" -> Per(Num1, Num2)
            "mod" -> Mod(Num1, Num2)
            "exp" -> Exp(Num1!!.toInt(), Num2!!.toInt())
            "sin" -> Sin(Num1)
            "cos" -> Cos(Num1)
            "tan" -> Tan(Num1)
            "log" -> Log10(Num1)
            "ln" -> Ln(Num1)
            "√" -> Sqrt(Num1)
            "e^" -> e(Num1)
            "π" -> PI_Mul(Num1)
            "10^" -> PowTen(Num1)
        }
        println("After " + x + "  Num1 " + Num1)
        display.text = Num1.toString()
        memory.text = Num1.toString()
        Num2 = null
        Operator = null
    }

    fun Add(x: Double?, y: Double?) {
        Num1 = (x!! + y!!)
    }

    fun Div(x: Double?, y: Double?) {
        Num1 = (x!! / y!!)
    }

    fun Mul(x: Double?, y: Double?) {
        Num1 = (x!! * y!!)
    }

    fun Pow(x: Double?, y: Double?) {
        Num1 = (x!!.pow(y!!.toInt()))
    }

    fun Per(x: Double?, y: Double?) {
        Num1 = (x!! * y!!) / 100
    }

    fun Mod(x: Double?, y: Double?) {
        Num1 = (x!! % y!!)
    }

    fun Sub(x: Double?, y: Double?) {
        Num1 = (x!! - y!!)
    }

    fun Exp(x: Int?, y: Int?) {
        Num1 = (x.toString().padEnd(y!! + 1, '0')).toDouble()
    }

    fun Sin(x: Double?) {
        Num1 = sin(x!!.toDouble())
    }

    fun Cos(x: Double?) {
        Num1 = cos(x!!.toDouble())
    }

    fun Tan(x: Double?) {
        Num1 = tan(x!!.toDouble())
    }

    fun Ln(x: Double?) {
        Num1 = log(x!!.toDouble())
    }

    fun Log10(x: Double?) {
        Num1 = log10(x!!.toDouble())
    }

    fun Sqrt(x: Double?) {
        Num1 = sqrt(x!!)
    }

    fun e(x: Double?) {
        Num1 = exp(x!!.toDouble())
    }

    fun PI_Mul(x: Double?) {
        Num1 = PI * (x!!.toDouble())
    }

    fun PowTen(x: Double?) {
        Num1 = pow(10.0, x!!)
    }

    fun Clr() {
        display.text = "";
        Num1 = null
        Num2 = null
        Operator = null
    }
}
