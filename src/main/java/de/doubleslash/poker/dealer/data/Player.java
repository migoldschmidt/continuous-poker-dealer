package de.doubleslash.poker.dealer.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.doubleslash.poker.dealer.ActionProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@AllArgsConstructor
@Slf4j
public class Player implements CardReceiver, Serializable {

    private final String name;
    private Status status;
    private int stack;
    private int bet;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<Card> cards = new ArrayList<>();

    @JsonIgnore
    private final transient ActionProvider actionProvider;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public void bet(final int chips) {
        if (chips > stack) {
            bet = stack;
        } else if (chips > bet) {
            bet = chips;
        }
    }

    @Override
    public void takeCard(final Card card) {
        cards.add(card);
    }

    public void fold() {
        this.status = Status.FOLDED;
    }

    public int collectBet() {
        final int chips = bet;
        bet = 0;
        stack -= chips;
        log.info("Player {} stack: {}", name, stack);
        return chips;
    }

    public void addToStack(final int chips) {
        if (chips < 0) {
            throw new IllegalArgumentException();
        }
        stack += chips;
    }

    @JsonIgnore
    public boolean isAllIn() {
        return status.equals(Status.ACTIVE) && bet == stack;
    }

    public boolean isGoingAllIn(final int potentialBet) {
        // convert to long for this check to avoid integer overflows
        return status.equals(Status.ACTIVE) && (long)bet + (long)potentialBet >= stack;
    }

    public void out() {
        this.status = Status.OUT;
    }

    public void active() {
        this.status = Status.ACTIVE;
    }

    public void clearCards() {
        cards.clear();
    }
}
