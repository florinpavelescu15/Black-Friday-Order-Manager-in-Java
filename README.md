# Manager de comenzi de Black Friday in Java
ALGORITMI PARALELI SI DISTRIBUITI,
Tema #2,
Pavelescu Florin, 334CC

## PREAMBUL
Rezolvarea temei mi-a luat aproximativ 5 ore. Tema mi s-a parut foarte 
interesanta si accesibila, fiind foarte asemanatoare cu cerintele din cadrul
laboratoarelor. Am urmat intocmai indicatiile din cerinta.

## DETALII DE IMPLEMENTARE
### Clasa `Tema2`, metoda `main()`
Preiau argumentele din linia de comanda, creez fisierele de iesire, initializez
thread-urile de nivel 1 si un threadpool cu numarul de maxim de workeri preluat
din linia de comanda (`N`). Impart fisierul de intrare `orders.txt`, in mod
echitabil, thread-urilor de nivel 1 (fiecare thread de nivel 1 primeste un numar
intreg de linii, deci un numar fix de comenzi) [1] si pornesc thread-urile de
nivel 1.

**Observatie:** Initializez si pornesc exact `N` thread-uri de nivel 1, unde `N` este
numarul maxim de thread-uri preluat din linia de comanda.

### Clasa `Leader` (thread de nivel 1)

#### Atributele clasei
- `private String folderInput` -> numele directorului in care se gasesc fisierele
de intrare;
- `private int start` -> indexul de unde incepe citirea din `RandomAccessFile` de
care este responsabil thread-ul;
- `private int end` -> indexul unde se termina citirea din din `RandomAccessFile` de
care este responsabil thread-ul;
- `private ExecutorService tpe` -> threadpool in care toate thread-urile de nivel 1
adauga task-uri (thread-uri de nivel 2).

#### Metoda `run()`
Folosesc un `StringTokenizer` pentru a extrage de pe fiecare dintre liniile
repartizate thread-ului curent id-ul comenzii si numarul de produse (`P`).

Pentru fiecare comanda extrasa:
- initializez un semafor care asteapta `P` apeluri `release()`, inainte de a merge
mai departe;
- adaug, pentru fiecare produs din comanda, cate un task in threadpool (fiecare
produs are un index de la `0` la `P - 1`, unde `P` este numarul de
produse din comanda curenta);
- astept, apeland `acquire()` pe semaforul creat anterior, ca toate cele `P` task-uri
introduse in threadpool sa se finalizele (toate cele `P` thread-uri
de nivel 2 sa dea `release()` pe semafor), practic sa se proceseze toate produsele
din comanda;
- scriu comanda in fisierul de iesire cu mentiunea "shipped" si trec la urmatoarea
comanda.

**Observatie:** Comenzie primite de un thread de nivel 1 se proceseaza secvential.
Daca un thread de nivel 1 a primit liniile `i, i + 1, ..., i + n`, se va
procesa mai intai comand `i`, apoi comanda `i + 1` s.a.m.d. Produsele din fiecare
comanda se proceseaza insa paralel, cu ajutorul threadpool-ului cu numar fix
de workeri.

### Clasa `Task` (thread de nivel 2)
#### Atributele clasei
`private String folderInput` ->  numele directorului in care se gasesc
fisierele de intrare;
`private String orderID` -> id-ul comenzii din care face parte produsul de care
se ocupa thread-ul;
`private int productIndex` -> indexul produsului de care se ocupa thread-ul;
`private Semaphore sem` -> semafor prin care thread-ul de nivel 2 anunta thread-ul
parinte de nivel 1 ca si-a terminat treaba.

#### Metoda `run()`
Parcurg fisierul `order_products.txt`, linie cu linie, pana gasesc al `productIndex`
-lea produs din comanda (practic, a `productIndex`-a linie din fisierul 
`order_products.txt` care contine id-ul comenzii). In momentul in care gasesc
produsul cautat, il scriu in fisierul de iesire cu mentiunea "shipped", anunt
thread-ul parinte de nivel 1 ca produsul a fost procesat prin apelul `release()` pe
semafor si opresc cautarea.

**Observatie:** Pentru a evita situatia in care mai multe thread-uri scriu simultan
in fisierele de iesire, am folosit blocuri `synchronized`.

Mai multe detalii despre implementare se gasesc in comentariile din cod.

[1] Mai multe detalii in `README_BONUS`.
