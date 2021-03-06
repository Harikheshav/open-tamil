-------------------------------
Mar 31, 2018

Developing a unit-distance graph of words.

G(V,E) where V is all words in vocabulary and Ei,j is an allowed edge when Vi, Vj have unit edit distance.

Such a graph G, can have property that we may find all the words which are
capable of being reached by a series of 1-edit-distance hops.

Reachability is symmetric : Vi <-> Vj, and particular shortest path is found
using Djikstra.

We may find answers to whether there is any reachability at all for a given
word pair Vi <-> Vj. We can form a meta-graph G2 which is biartite and connects words which are only reachable from each other - really this will become some kind of clustering of the vocabulary as islands of equivalence classes. Since Va - Vb are reachable and then by induction all of the nodes reachable from Va or Vb are also reachable from the nodes Va, Vb and path (Va, Vb) forming an equivalence class.

e.g. Such graphs G, G2 can be used to form the decisions / classification of
whether words Vi, Vj are reachable. In G it will be existence of a path.
In G2 it will be if Vi, Vj belong to the same "node" or equivalence class.

63896**2
4082698816
>>> 63896*(63895)/2.0
2041317460.0


Measures of a spelling checker:
-------------------------------
Dec 15, 2017
1) Posted to thamizha group. Plan to get some attention to this project FWIW.
2) Need a rule based spell-checker
3) Need to port rules from Affix files of Hunspell/Aspell
4) Need to port stemmer from snowball code
5) [OPTIM] reduce the number of times tamil.utf8.get_letters is called by speller
6) [OPTIM] profile the code to see performance of Norvig suggestor; it maybe slowing us down! Potentially update Norvig suggestor using the non-alpha but stats suggested uni/bigram order of alternative searches
7) Pursue ML related discrimination of word alternates
   ML problem 1: Is this given sequence of Tamil letters represent an English word transliterated ? [Y/N]
   ML problem 2: Is this sequence of Tamil letters represent a suffix/acceptable punarchi ?

Dec 12, 2017
1) Atom editor has spell check module which can be integ target.
2) Punarchi removal can be part of the setup.
3) Case conjugation rules are important to be added
4) Homebrew stemmer is also important
5) Final product can be deployed as webserver
6) Configuration for DICTIONARY/word-list is useful.

-------------------------------
0) Need language statistical data.

1) non-word error : word does not exist in dictionary:
e.g. "he lievs in the house"
         ^^^^^

2) real word error: word is in dictionary but not the right one - i.e. it could be out of context.
e.g. "he lives ink the house"
               ^^^

3) Precision - rate of correct detection
   ( 1 - Precision ) gives False alarm

4) Recall - rate of correct detection over actual number of errors

5) Errors made by system : total detection - correct detection

6) Synthetic error generation.

7) Spell checkers introduce a real-word error inplace of a detected non-word error.

8) Error model of spelling errors in Tamil (any language) is key to detect the patterns
of mistake generation, and coming with a strategy
to resolve the same.

Ref: P. Samanta, B. Chaudhari, "A simple real-word error detection and correction using local word
bigram and trigram" (2013)
