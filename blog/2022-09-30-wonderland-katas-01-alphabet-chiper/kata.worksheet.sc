
// first, we generate the alphabet
val alphabet = 'a' to 'z'
// alphabet: Inclusive[Char] = NumericRange a to z

// also, the kata requires a keyword and a message to cipher
val keyword = "void"
// keyword: String = void
val message = "meetme"
// message: String = meetme

// then, we need to pair the char os the keyword and the message
// i.e.
// (v,m) (o,e) (i,e) (d,t) (v,m) (o,e)
// note that in this example the message is longer than the keyword
// from the top of my head, I would zip both: keyword and message
keyword.zip(message)
// res0: IndexedSeq[Tuple2[Char, Char]] = Vector((v,m), (o,e), (i,e), (d,t))

// interesting enough 🤔 our zipped result is truncated to the lenght
// of the shorter string, in this case the keyword.
// this means that we need to write our own zipping function or
// unfold a list based on our message or write interable.

// the trick in this function is the reusage of the keyword when
// we have exahusted it and we have still more char in the message
def zip(message: String, keyword: String) = {

  def go(msg: List[Char], key: List[Char], acc: List[(Char, Char)]): List[(Char, Char)] =
    (msg, key) match
      case (Nil, _)           => acc
      case (c :: cs, k :: ks) => go(cs, ks, acc :+ (k, c))
      case (_ :: _, Nil)      => go(msg, keyword.toList, acc) // <-- the trick

  go(message.toLowerCase().toList, keyword.toLowerCase().toList, List.empty)
}

zip(message, keyword)
// res1: List[Tuple2[Char, Char]] = List((v,m), (o,e), (i,e), (d,t), (v,m), (o,e))

// now that we are able to zip our strings, we also need a
// function to generate an alphabet starting from a given char
// i.e. mnopqrstuvwxyzabcdefghijkl - alphabet starting at m
def shiftAt(alphabet: String, c: Char): String =
  val (left, right) = alphabet.splitAt(alphabet.indexOf(c))
  right ++ left

assert(shiftAt(alphabet.mkString, 'm') == "mnopqrstuvwxyzabcdefghijkl")

// fixing our function to the only alphabet we have
val alphabetAt = (shiftAt _).curried.apply(alphabet.mkString)

// making sure our alphabetAt function works as epxected
assert(alphabetAt('m') == "mnopqrstuvwxyzabcdefghijkl")

// now, we should be able to build the matrix of chars required
val matrix = alphabet
  .map(c => alphabetAt(c).toVector)

// informative only!
matrix
  .map(_.mkString(" "))
  .mkString(System.lineSeparator())
/**
res4: String =
a b c d e f g h i j k l m n o p q r s t u v w x y z
b c d e f g h i j k l m n o p q r s t u v w x y z a
c d e f g h i j k l m n o p q r s t u v w x y z a b
d e f g h i j k l m n o p q r s t u v w x y z a b c
e f g h i j k l m n o p q r s t u v w x y z a b c d
f g h i j k l m n o p q r s t u v w x y z a b c d e
g h i j k l m n o p q r s t u v w x y z a b c d e f
h i j k l m n o p q r s t u v w x y z a b c d e f g
i j k l m n o p q r s t u v w x y z a b c d e f g h
j k l m n o p q r s t u v w x y z a b c d e f g h i
k l m n o p q r s t u v w x y z a b c d e f g h i j
l m n o p q r s t u v w x y z a b c d e f g h i j k
m n o p q r s t u v w x y z a b c d e f g h i j k l
n o p q r s t u v w x y z a b c d e f g h i j k l m
o p q r s t u v w x y z a b c d e f g h i j k l m n
p q r s t u v w x y z a b c d e f g h i j k l m n o
q r s t u v w x y z a b c d e f g h i j k l m n o p
r s t u v w x y z a b c d e f g h i j k l m n o p q
s t u v w x y z a b c d e f g h i j k l m n o p q r
t u v w x y z a b c d e f g h i j k l m n o p q r s
u v w x y z a b c d e f g h i j k l m n o p q r s t
v w x y z a b c d e f g h i j k l m n o p q r s t u
w x y z a b c d e f g h i j k l m n o p q r s t u v
x y z a b c d e f g h i j k l m n o p q r s t u v w
y z a b c d e f g h i j k l m n o p q r s t u v w x
z a b c d e f g h i j k l m n o p q r s t u v w x y
*/

// we are ready to encode our message!
// 1 - gen pair of char (zipping message and keyword)
// 2 - folding our list of pair to generate a single string with the encoded chars
//     notice how we are not using the matrix above (alt we can use the matrix)
//     a - we generate the alphabe we need, and then
//     b - get the index of the char at hand in the original alphabet
def encode(msg: String, key: String) =
  zip(msg, key) // 1
    .foldRight("") { case ((c, k), acc) => // 2
      alphabetAt(k) // 2.a
        .charAt(
          alphabet.indexOf(c) // 2.b
        ) +: acc
    }

// our encoded function seems to work but
// we need also the decode function in order to prove that
val encoded = encode(message, keyword)
assert("hsmwhs" == encoded)

// it time for the decode function
// decode function implementation is similar to
// the encode function
def decode(encoded: String, keyword: String) =
  zip(encoded, keyword)
    .foldRight("") { case ((c, k), acc) =>
      alphabet.mkString.charAt(
        shiftAt(alphabet.mkString, c).indexOf(k)
      ) +: acc
    }

val decoded = decode(encoded, keyword)
assert(message == decoded)

// finally, the last piece of the kata is a cipher function
// to find the keyword given a encoded and decoded message
def cipher(decoded: String, encoded: String) =
  zip(decoded, encoded)
    .map { case (e, o) =>
      matrix
        .find(abc => {
          abc.indexOf(e) == alphabet.indexOf(o)
        })
        .get
        .head
    }
    .distinct
    .mkString

val key = cipher(decoded, encoded)
assert(keyword == key)