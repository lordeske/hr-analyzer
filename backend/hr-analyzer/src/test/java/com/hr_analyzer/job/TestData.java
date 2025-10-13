package com.hr_analyzer.job;

import com.hr_analyzer.auth.model.Role;
import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.job.model.Job;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TestData {


    public static Job job(String title)
    {

        User user = new User(
                1L,"Mihajlo","Eskic", "eske@gmail.com",
                "066666666","sifraTest", Role.HR
        );

        return new Job(
                1L, title, "Kompanija", "Beograd",
                "Opis", LocalDateTime.now(),
                user, null, BigDecimal.valueOf(3000)
        );



    }

    public static User user()
    {
      return  new User(
            1L,"Mihajlo","Eskic", "eske@gmail.com",
            "066666666","sifraTest", Role.HR);

    }


}
