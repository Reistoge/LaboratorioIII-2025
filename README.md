# 🧪 Laboratorio III – Patrones de Software y Programación

Fecha de Entrega: **01/07/2025**

Integrantes:
* Ferran Rojas Andreu. 
* Maximo Sarno Jimenez.

## Objetivo

El objetivo de este laboratorio es que el/la estudiante sea capaz de diseñar y ejecutar **pruebas unitarias** utilizando **JUnit 4**, aplicando buenas prácticas en cobertura, validación de excepciones y organización del código de testeo.

Además, se deberá generar un **informe automático de cobertura de código** utilizando **JaCoCo**.

---

## Requisitos

- El laboratorio debe implementarse usando **JUnit 4**.
- Se deben escribir exactamente **10 casos de prueba** para la clase `LoyaltyDiscountEngine`.
- Se debe incluir al menos **un caso que valide una excepción**.
- Se debe implementar una clase que agrupe **todos los tests en una suite**.
- Se debe ejecutar el proyecto con Maven y generar el **informe de cobertura con JaCoCo**.

---

## Casos de prueba obligatorios

A continuación se listan los **10 casos mínimos** que deben estar presentes:

| # | Escenario | Descripción |
|---|-----------|-------------|
| 1 | Descuento nulo | Cliente básico, sin pedidos, sin antigüedad, sin promo. Esperado: `0.0` |
| 2 | Cliente SILVER | Cliente con nivel SILVER, 3 años, 50 órdenes. Esperado: `0.05` |
| 3 | Antigüedad > 5 años | Cliente GOLD con 6 años, 10 órdenes. Esperado: `0.15` |
| 4 | Más de 100 órdenes | Cliente GOLD con 101 órdenes. Esperado: `0.15` |
| 5 | Promoción activa | Cliente BASIC con promoción activa. Esperado: `0.10` |
| 6 | Descuento máximo | Cliente PLATINUM con 10 años, 150 órdenes, promoción activa. Esperado: `0.30` |
| 7 | Registro en logs | Verificar que se registre correctamente una operación en logs. |
| 8 | Búsqueda por ID | Usar `computeDiscountById()` para calcular descuento. |
| 9 | Excepción por nulo | Llamar a `computeDiscount(null)` y verificar `IllegalArgumentException` |
| 10| Excepción por ID faltante | Cliente sin ID. Esperado: `IllegalArgumentException` |

---

## 🔍 Cobertura de código

Se debe generar un informe de cobertura de código utilizando el plugin **JaCoCo**. Esto permite visualizar qué líneas y métodos han sido ejecutados por los casos de prueba y cuál es el porcentaje total de cobertura.

### 📦 Configuración (en `pom.xml`)

Asegúrate de tener el siguiente plugin de JaCoCo en la sección `<build>`:

```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.12</version>
  <executions>
    <execution>
      <goals>
        <goal>prepare-agent</goal>
      </goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>verify</phase>
      <goals>
        <goal>report</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

##  🧪 Generación del informe
Ejecuta los tests y genera el reporte con:

``` bash
mvn clean verify
```

Al finalizar, el informe HTML de cobertura estará disponible en:

``` bash
target/site/jacoco/index.html
```

Ábrelo con tu navegador para revisar qué clases y métodos han sido cubiertos por las pruebas.

**Nota:** Recuerda que los casos de test van en la carpeta  ```test/java```, y que las clases de test terminan con
```Test (Ej: CasoTest)``` y los métodos empiezan con ```test, (Ej: testCobertura)```.

---
## 📊 Evaluación

La siguiente rúbrica será utilizada para evaluar el laboratorio:

| Criterio                                              | Puntaje |
|-------------------------------------------------------|---------|
| ✅ Implementación de los **10 casos de prueba**        | 4.0 pts |
| 🧪 Agrupar todos los tests                            | 1.0 pts |
| 🔍 **Cobertura** igual o superior al 80 % con JaCoCo  | 2.0 pts |
| ⚠️ Manejo correcto de excepciones | 1.0 pts |
| ⚙️ Proyecto compila y ejecuta correctamente con Maven | 2.0 pts |
| **Total**                                             | **10 pts** |


