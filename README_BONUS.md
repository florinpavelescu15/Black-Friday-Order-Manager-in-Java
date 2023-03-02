-------------------------Pavelescu Florin, 334CC----------------------------
-------------------ALGORITMI PARALELI SI DISTRIBUITI------------------------
-------------------------------Tema #2-------------------------------------- 
----------------Manager de comenzi de Black Friday in Java------------------
----------------------------------------------------------------------------
--------------------------------BONUS---------------------------------------
-----------------------DETALII DE IMPLEMENTARE------------------------------
Pentru ca fiecare thread de nivel 1 sa se ocupe de o sectiune din fisierul
orders.txt, fara sa il citeasca pe tot, am folosit metodele din clasa
RandomAccessFile.

1. Clasa Tema2, metoda main():
Am creat un obiect de tip RandomAccessFile din fisierul orders.txt, am aflat
dimensiunea in bytes a fisierului si l-am impartit in mod egal thread-urilor
de nivel 1, cu ajutorul formulelor din laborator, usor modificate.
Pentru thread-ul i de nivel 1 avem
    start = i * N / P
    end = min((i + 1) * N / P, N)
unde N este dimensunea fisierului in bytes si P este numarul de thread-uri,
dar exista posibilitatea ca start sau end sa nu reprezinte un inceput
respectiv un final de linie. Astfel, necunoscand dinainte lungimea unei
linii din orders.txt, am verificat daca start este la inceputul unei linii
si, daca nu, am citit cate un byte incepand de la start pana la prima aparitie
lui \n, caz in care ma aflam la inceputul liniei imediat urmatoare si am
actualizat valoarea lui start. Analog am procedat si pentru end.
Pe scurt, atat pentru start, cat si pentru end, le-am mutat spre derapta
pana cand start a ajuns la inceput de linie si end la final de linie.

2. Clasa Leader, metoda run():
Am creat un obiect de tip RandomAccessFile din fisierul orders.txt. Cu start
si end primite din main() am mutat pointerul de inceput al obiectului la
pozitia start si, incepand de acolo, am citit exact end - start bytes intr-un
vector de bytes, pe care l-am transformat in String si l-am folosit ulterior.
