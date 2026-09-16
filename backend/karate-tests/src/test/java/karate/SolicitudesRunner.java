package karate;

import com.intuit.karate.junit5.Karate;

class SolicitudesRunner {

    @Karate.Test
    Karate testCicloVida() {
        return Karate.run("classpath:features/solicitudes/ciclo-vida-solicitud.feature");
    }
}
