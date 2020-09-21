package server.services;

import com.google.common.hash.Hashing;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import server.data.DAOs.ConversationDAO;
import server.data.DTOs.ConversationTO;
import server.data.DTOs.DHRequestTO;
import server.repositories.ConversationRepository;
import server.utility.exceptions.DHException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Random;

@Service
public class DHService {

    private final Random random = new Random();
    @Autowired
    private ConversationRepository conversationRepository;

    private Long generatePrimaryNumberLong(long from, long to) {

        long p = 0L;
        boolean bol = true;
        while (bol) {
            if (p > from && p < to && checkIfPrimary(p)) {
                bol = false;
            } else {
                p = random.nextInt() % to + from;
            }
        }
        return p;
    }

    private boolean checkIfPrimary(Long number) {
        if (number == 1) {
            return true;
        }
        double root = Math.sqrt(number);
        double i = 2.0;
        while (i < root) {
            if (number % i == 0) return false;
            i += 1;
        }
        return true;
    }

    // Iterative Function to calculate (x^y)%p in O(logy)
    private long power(long x, long y, long p) {
        long res = 1;     // Initialize result

        x = x % p; // Update x if it is more than or
        // equal to p

        while (y > 0) {
            // If y is odd, multiply x with result
            if (y % 2 == 1) {
                res = (res * x) % p;
            }

            // y must be even now
            y = y >> 1; // y = y/2
            x = (x * x) % p;
        }
        return res;
    }

    // Function to find smallest primitive root of n
    private long findPrimitive(long n) {
        HashSet<Long> s = new HashSet<>();

        // Check if n is prime or not
        if (!checkIfPrimary(n)) {
            return -1;
        }

        // Find value of Euler Totient function of n
        // Since n is a prime number, the value of Euler
        // Totient function is n-1 as there are n-1
        // relatively prime numbers.
        long phi = n - 1;

        // Find prime factors of phi and store in a set
        findPrimeFactors(s, phi);

        // Check for every number from 2 to phi
        for (int r = 2; r <= phi; r++) {
            // Iterate through all prime factors of phi.
            // and check if we found a power with value 1
            boolean flag = false;
            for (Long a : s) {

                // Check if r^((phi)/primefactors) mod n
                // is 1 or not
                if (power(r, phi / (a), n) == 1) {
                    flag = true;
                    break;
                }
            }

            // If there was no power with value 1.
            if (!flag) {
                return r;
            }
        }

        // If no primitive root found
        return -1;
    }

    // Utility function to store prime factors of a number
    private void findPrimeFactors(HashSet<Long> s, long n) {
        // Print the number of 2s that divide n
        while (n % 2 == 0) {
            s.add(2L);
            n = n / 2;
        }

        // n must be odd at this point. So we can skip
        // one element (Note i = i +2)
        for (long i = 3; i <= Math.sqrt(n); i = i + 2) {
            // While i divides n, print i and divide n
            while (n % i == 0) {
                s.add(i);
                n = n / i;
            }
        }

        // This condition is to handle the case when
        // n is a prime number greater than 2
        if (n > 2) {
            s.add(n);
        }
    }

//    Long p = generatePrimaryNumberLong(10000, 99999);
//    Long g = findPrimitive(p);
//    Long _s = generatePrimaryNumberLong(10, 100);
//        dhRepository.findById(userID)
//            .ifPresent((e) -> dhRepository.delete(e));
//    long S = power(g, _s, p);
//        dhRepository.save(DHDAO.builder()
//                ._id(new ObjectId(userID))
//            .p(p)
//                .g(g)
//                ._s(_s)
//                .S(S)
//                .build());

    public Pair<ConversationTO, Long> handleCrypto(DHRequestTO dhRequestTO, boolean caller) {
        Long _s = generatePrimaryNumberLong(10, 100);
        Long p = dhRequestTO.getP();
        Long g = dhRequestTO.getG();
        Long A = dhRequestTO.getA();

        long S = power(g, _s, p);
        long secret = power(A, _s, p);

        String key = (caller)
                ? generateKey()
                : conversationRepository.findById(dhRequestTO.getID())
                .orElseThrow(DHException::new)
                .getKey();

        if (caller) {
            return Pair.of(ConversationTO.builder()
                            .S(S)
                            .key(key)
                            .build(),
                    secret);
        } else {
            return Pair.of(ConversationTO.builder()
                            .S(S)
                            .ID(dhRequestTO.getID())
                            .key(key)
                            .build(),
                    secret);
        }
    }


    public String encryptKey(String secret, String key) {

//        byte[] iv = {'1','0','1','0','1','0','1','0', '1','0','1','0','1','0','1','0'};
//        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        try {
            secret = secret + "0000000000000000";
            secret = secret.substring(0, 16);
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
//            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(key.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new DHException("Cannot perform ciphering " + e.getMessage());
        }
    }

    @NotNull
    @SuppressWarnings("UnstableApiUsage")
    private String generateKey() {
        return Hashing.sha256()
                .hashString(String.valueOf(System.currentTimeMillis()), StandardCharsets.UTF_8)
                .toString();
    }
}