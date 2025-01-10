import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        Scanner scn = new Scanner(System.in);
        String prefix = getAccountPrefix(scn);

        if (prefix == null)
        {
            System.out.println("Nieprawidłowe dane. Program zakończy działanie.");
            return;
        }

        //URL
        String resourceUrl = "https://ewib.nbp.pl/plewibnra?dokNazwa=plewibnra.txt";

        try
        {
            //przetwarzanie danych z serwera
            processBankData(resourceUrl, prefix);
        }
        catch (Exception e)
        {
            System.out.println("Wystąpił błąd podczas przetwarzania danych: " + e.getMessage());
        }
        finally
        {
            scn.close();
        }
    }

    //metoda do pobierania i walidacji danych wejściowych
    private static String getAccountPrefix(Scanner scn)
    {
        System.out.print("Wprowadź trzy pierwsze cyfry numeru konta: ");
        String prefix = scn.nextLine();

        if (!prefix.matches("\\d{3}"))
        {
            System.out.println("Nieprawidłowe dane. Podaj dokładnie trzy cyfry.");
            return null;
        }
        return prefix;
    }

    //metoda do przetwarzania danych z serwera
    private static void processBankData(String resourceUrl, String prefix) throws Exception
    {
        //połączenie HTTP
        HttpURLConnection connection = (HttpURLConnection) new URL(resourceUrl).openConnection();
        connection.setRequestMethod("GET");

        //odczyt danych z serwera
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream())))
        {
            String currentLine;
            boolean matchFound = false;

            while ((currentLine = bufferedReader.readLine()) != null)
            {
                if (currentLine.startsWith(prefix))
                {
                    //przetwarzanie danych
                    String[] data = currentLine.split("\\t");
                    if (data.length >= 2)
                    {
                        System.out.println("Kod banku: " + data[0]);
                        System.out.println("Nazwa banku: " + data[1]);
                        matchFound = true;
                    }
                    break;
                }
            }

            if (!matchFound)
            {
                System.out.println("Nie znaleziono banku odpowiadającego podanym cyfrom.");
            }
        }
        catch (Exception e)
        {
            throw new Exception("Błąd podczas odczytu danych z serwera: " + e.getMessage());
        }
    }
}