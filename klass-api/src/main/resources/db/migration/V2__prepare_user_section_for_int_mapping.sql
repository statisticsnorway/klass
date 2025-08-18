UPDATE klass."user"
SET section = CASE
    -- Split code preview: sections containing ' - '
                  WHEN section LIKE '% - %' THEN split_part(section, ' - ', 1)

    -- Already number preview: sections that are only digits
                  WHEN section ~ '^\d+$' THEN split_part(section, ' - ', 1)

    -- Special case 1: sections like 'O 330...'
                  WHEN section ILIKE '%O 330%' THEN split_part(section, ' ', 2)

    -- Special cases anywhere in the string: map to '724'
                  WHEN section ILIKE ANY (ARRAY[
                      '%Utdannings- og kulturstatistikk%',
                      '%Seksjon for helse-, omsorg- og sosialstatistikk%',
                      '%Offentlige finanser%',
                      '%Næringslivets utvikling%',
                      '%Næringslivets strukturer%',
                      '%Næringslivets konjunkturer%',
                      '%Inntekt og levekår%',
                      '%Helse-, omsorg- og sosialstatistikk%',
                      '%Finansmarkedsstatistikk%',
                      '%Energi, miljø- og transportstatistikk%',
                      '%Eiendoms-, areal- og primærnæringsstatistikk%',
                      '%Brukerinnsikt og webutvikling%',
                      '%Befolkningsstatistikk%'
                      ]) THEN '724'

    -- Keep original if none match
                  ELSE section
    END;
