#include <iostream>
#include <cmath> 
using namespace std;

// Klasa bazowa
class Figura {
public:
    virtual double obliczPole()= 0; 
    virtual double obliczObwod()= 0; 
    virtual void wypiszDane()= 0; 
    virtual ~Figura() {} // Wirtualny destruktor
};

//Obliczanie prostokąt
class Prostokat : public Figura {
private:
    double dlugosc;
    double szerokosc;

public:
    Prostokat(double p_dlugosc, double p_szerokosc) {//Konstruktor parametryzowany musi mieć tą samą nazwę co nazwa klasy
        dlugosc = p_dlugosc;
        szerokosc = p_szerokosc;
    }

    double obliczPole() override {
        return dlugosc * szerokosc;
    }

    double obliczObwod()  override {
        return 2 * (dlugosc + szerokosc);
    }

    void wypiszDane() override {
        cout << "Prostokat: Dlugosc = " << dlugosc << ", Szerokosc = " << szerokosc << "\n";
        cout << "Pole: " << obliczPole() << ", Obwod: " << obliczObwod() << "\n";
    }
};

// Oblicznie kola
class Kolo : public Figura {
private:
    double promien;

public:
    Kolo(double p_promien){
    promien=p_promien;
    }

    double obliczPole()  override {
        return M_PI * promien * promien;
    }

    double obliczObwod()  override {
        return 2 * M_PI * promien;
    }

    void wypiszDane()  override {
        cout << "Kolo: Promien = " << promien << "\n";
        cout << "Pole: " << obliczPole() << ", Obwod: " << obliczObwod() << "\n";
    }
};


// Obliczanie trójkąta
class Trojkat : public Figura {
private:
    double bok1;
    double bok2;
    double bok3;

public:
    Trojkat(double p_bok1, double p_bok2, double p_bok3) {
        bok1 = p_bok1;
        bok2 = p_bok2;
        bok3 = p_bok3;
    }

    double obliczPole() override {
        // Wzór Herona: S = sqrt(s * (s - bok1) * (s - bok2) * (s - bok3))
        double s = (bok1 + bok2 + bok3) / 2;
        return sqrt(s * (s - bok1) * (s - bok2) * (s - bok3));
    }

    double obliczObwod() override {
        return bok1 + bok2 + bok3;
    }

    void wypiszDane() override {
        cout << "Trojkat: Boki = " << bok1 << ", " << bok2 << ", " << bok3 << "\n";
        cout << "Pole: " << obliczPole() << ", Obwod: " << obliczObwod() << "\n";
    }
};

//Obliczanie rownolegloboku
class Rownoleglobok : public Figura {
private:
    double podstawa;
    double wysokosc;
    double bok;

public:
    Rownoleglobok(double p_podstawa, double p_wysokosc, double p_bok) {
        podstawa = p_podstawa;
        wysokosc = p_wysokosc;
        bok = p_bok;
    }

    double obliczPole() override {
        return podstawa * wysokosc;
    }

    double obliczObwod() override {
        return 2 * (podstawa + bok);
    }

    void wypiszDane() override {
        cout << "Rownoleglobok: Podstawa = " << podstawa << ", Bok = " << bok << ", Wysokosc = " << wysokosc << "\n";
        cout << "Pole: " << obliczPole() << ", Obwod: " << obliczObwod() << "\n";
    }
};



//Oblicznie rombu
class Romb : public Figura {
private:
    double d1;
    double d2;
    double bok;

public:
    Romb(double p_d1, double p_d2, double p_bok) {
        d1 = p_d1;
        d2 = p_d2;
        bok = p_bok;
    }

    double obliczPole() override {
        return 0.5 * d1 * d2;
    }

    double obliczObwod() override {
        return 4 * bok;
    }

    void wypiszDane() override {
        cout << "Romb: Przekatne = " << d1 << ", " << d2 << ", Bok = " << bok << "\n";
        cout << "Pole: " << obliczPole() << ", Obwod: " << obliczObwod() << "\n";
    }
};

//Obliczanie szescianu
class Szescian : public Figura {
private:
    double a;

public:
    Szescian(double p_a) {
        a = p_a;
    }

    double obliczPole() override {
        return 6 * a * a;
    }

    double obliczObwod() override {
        return 12 * a;
    }

    void wypiszDane() override {
        cout << "Szescian: Krawedz = " << a << "\n";
        cout << "Pole: " << obliczPole() << ", Obwod: " << obliczObwod() << "\n";
    }
};
int main() {
    int wybor;
    Figura* figura = nullptr;

    cout << "Wybierz figure: \n";
    cout << "1. Prostokat\n";
    cout << "2. Kolo\n";
    cout << "3. Trojkat\n";
    cout << "4. Rownoleglobok\n";
    cout << "5. Romb\n";
    cout << "6. Szescian\n";
    cout << "Podaj numer figury: ";
    cin >> wybor;

    switch(wybor) {
        case 1: {
            double dlugosc, szerokosc;
            cout << "Podaj dlugosc prostokata: ";
            cin >> dlugosc;
            cout << "Podaj szerokosc prostokata: ";
            cin >> szerokosc;
            figura = new Prostokat(dlugosc, szerokosc);
            break;
        }
        case 2: {
            double promien;
            cout << "Podaj promien kola: ";
            cin >> promien;
            figura = new Kolo(promien);
            break;
        }
        case 3: {
            double bok1, bok2, bok3;
            cout << "Podaj dlugosci bokow trojkata: ";
            cin >> bok1 >> bok2 >> bok3;
            figura = new Trojkat(bok1, bok2, bok3);
            break;
        }
        case 4: {
            double podstawa, wysokosc, bok;
            cout << "Podaj podstawe, wysokosc i bok rownolegloboku ";
            cin >> podstawa >> wysokosc >> bok;
            figura = new Rownoleglobok(podstawa, wysokosc, bok);
            break;
        }
        case 5: {
            double d1, d2, bok;
            cout << "Podaj przekatne i bok rombu: ";
            cin >> d1 >> d2 >> bok;
            figura = new Romb(d1, d2, bok);
            break;
        }
         case 6: {
            double a;
            cout << "Podaj krawedz szescianu: ";
            cin >> a;
            figura = new Szescian(a);
            break;
        }
        default:
            cout << "Niepoprawny wybor!" << endl;
            return 1;
    }

    figura->wypiszDane();
    delete figura; // Usuwanie obiektu

    return 0;
}
