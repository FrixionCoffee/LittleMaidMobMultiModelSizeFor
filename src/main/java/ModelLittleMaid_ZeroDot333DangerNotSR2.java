import firis.lmmm.api.caps.IModelCaps;
import firis.lmmm.api.caps.ModelCapsHelper;
import firis.lmmm.api.model.ModelLittleMaidBase;
import firis.lmmm.api.renderer.ModelRenderer;

import java.util.Optional;

/**
 * Javaソースコードライセンス, リソースライセンスはMMM氏の規約↓と同じ
 * ----------------------------------------------------
 * ・テクスチャーを作ってくれた方々に感謝の祈りを捧げてください。
 * ・動画等での使用、改造、転載すきにしてもよいのよ？
 * ・ただし、商用利用は除く。
 * ・あと、いかなる意味でも作者は責任をとりませぬ。
 * ----------------------------------------------------
 * <p>
 */
public class ModelLittleMaid_ZeroDot333DangerNotSR2 extends ModelLittleMaidBase {
    public ModelRenderer eyeR;
    public ModelRenderer eyeL;

    /**
     * メイドさんの拡大率を制御するためのEnum
     * インナークラスとして存在しているので、保持元クラスの変数に入れれば動く
     * egg) private static final SizeRate sizeRate = SizeRate.ZERO_DOT500;
     * 上の例では0.5倍サイズのメイドさんになる<br><br>
     * <p>
     * 変数名にDANGERが付いているものは使用を推奨しないもの
     * 調整する式自体は結構適当で誤差が出まくりだが、0.5倍までであれば人間の目にはあまり違和感がない
     * 0.5未満になってくると少しの誤差で人間が気付ける様になる<br><br>
     * 追記：特に体が潰れたような形状になってしまうが、0.37倍くらいならそんなに悪くないかも。(0.25倍辺りは危ない)<br><br>
     * <p>
     * ちゃんとした数式から移動量を求めればきれいになるんだろうけど、導出が面倒なのでしない。(正直0.125倍とかはネタレベル)<br><br>
     * <p>
     * このクラスはEnumなのでSizeRate sizeRate = new SizeRate(1.0f);
     * みたいに初期化・インスタンス化できません。
     */
    private enum SizeRate {
        /**
         * DEPRECATEDは人間が容認できないレベルの位置ズレが起きるもの
         *
         * @deprecated
         */
        @Deprecated
        ZERO_DOT125_DEPRECATED(0.125f),

        @SuppressWarnings("unused")
        ZERO_DOT200_DANGER(0.2f),

        @SuppressWarnings("unused")
        ZERO_DOT250_DANGER(0.25f),

        @SuppressWarnings("unused")
        ZERO_DOT300_DANGER(0.3f),

        /**
         * この辺は自分がリトルメイドになる場合に使えそうなので残してるだけ感
         */
        @SuppressWarnings("unused")
        ZERO_DOT333_DANGER(0.333000F),

        @SuppressWarnings("unused")
        ZERO_DOT350_DANGER(0.35f),

        /**
         * 多分ハーフブロックを通過できるギリギリ
         */
        @SuppressWarnings("unused")
        PASSED_HALF_BLOCK_DANGER(0.370000F),

        @SuppressWarnings("unused")
        ZERO_DOT400_DANGER(0.4f),

        /**
         * 0.45倍までならボート窒息が起きない
         */
        @SuppressWarnings("unused")
        ZERO_DOT450(0.45f),

        /**
         * 使うならこの列挙か、これ以上の大きさ推奨
         */
        @SuppressWarnings("unused")
        ZERO_DOT500(0.5f),

        @SuppressWarnings("unused")
        ZERO_DOT550(0.55F),

        @SuppressWarnings("unused")
        ZERO_DOT600(0.6F),

        @SuppressWarnings("unused")
        ZERO_DOT650(0.65F),

        @SuppressWarnings("unused")
        ZERO_DOT700(0.7F),

        @SuppressWarnings("unused")
        PASSED_BLOCK(0.7333333F),

        @SuppressWarnings("unused")
        ZERO_DOT750(0.75f),

        @SuppressWarnings("unused")
        ZERO_DOT800(0.8f),

        @SuppressWarnings("unused")
        ZERO_DOT850(0.85f),

        @SuppressWarnings("unused")
        ZERO_DOT900(0.9f),

        @SuppressWarnings("unused")
        ZERO_DOT950(0.95f),

        /**
         * テスト用
         * 拡大率99.99%(ほとんど同一)
         * <p>
         * 人間だと違いはわからんと思う
         */
        @SuppressWarnings("unused")
        NEAR_EQUALS_TESTS(0.9999F),

        /**
         * これはメイドさんが正面でペラッペラになる<br>
         * 逆襲メイドさんMODを入れた状態のホラー演出ならいいかもだけど、癒やし面では用途無いと思う
         */
        @SuppressWarnings("unused")
        PAPER_SUPER_DANGER(1.0f, 1.0f, 0.01f),

        /**
         * これはメイドさんが横向きでペラッペラになる<br>
         * 癒やし面では用途無いと思う。足の動きがペパマリっぽくてそのうち参考になるかも(ならない)
         */
        @SuppressWarnings("unused")
        PAPER_Z_SUPER_DANGER(0.01f, 1.0f, 1.0f);

        /**
         * axisは軸ごとの拡大率を格納する変数
         */
        public final float axisX;
        /**
         * axisは軸ごとの拡大率を格納する変数
         */
        public final float axisY;
        /**
         * axisは軸ごとの拡大率を格納する変数
         */
        public final float axisZ;

        /**
         * rebirthAxisは縮小後に起きるズレを補正するための変数(1-拡大率が入る)
         */
        public final float rebirthAxisX;
        /**
         * rebirthAxisは縮小後に起きるズレを補正するための変数(1-拡大率が入る)
         */
        public final float rebirthAxisY;
        /**
         * rebirthAxisは縮小後に起きるズレを補正するための変数(1-拡大率が入る)
         */
        public final float rebirthAxisZ;

        /**
         * コンストラクタに一つだけ値を入れたときに呼ばれる
         * Enumなので強制private
         *
         * @param commonAxis 共通の拡大率
         */
        SizeRate(float commonAxis) {
            checkAxis(commonAxis);

            axisX = commonAxis;
            axisY = commonAxis;
            axisZ = commonAxis;

            rebirthAxisX = 1.0f - commonAxis;
            rebirthAxisY = 1.0f - commonAxis;
            rebirthAxisZ = 1.0f - commonAxis;
        }

        /**
         * @param axisX X軸の拡大率
         * @param axisY Y軸の拡大率
         * @param axisZ Z軸の拡大率
         */
        SizeRate(float axisX, float axisY, float axisZ) {
            checkAxis(axisX);
            checkAxis(axisY);
            checkAxis(axisZ);

            this.axisX = axisX;
            this.axisY = axisY;
            this.axisZ = axisZ;

            rebirthAxisX = 1.0f - axisX;
            rebirthAxisY = 1.0f - axisY;
            rebirthAxisZ = 1.0f - axisZ;
        }

        /**
         * 仕様上拡大率は0 < x <= 1なので仕様外を弾くための関数
         *
         * @param axis チェック対象の拡大率
         */
        private void checkAxis(float axis) {
            if (axis > 1.0f || axis <= 0.0f) {
                throw new IllegalArgumentException("");
            }
        }

    }

    /**
     * ModelCapsHelperのgetCapsValueはNullableなObjectを返すことがある。
     * Nullable時のダウンキャストやNPE回避が面倒なのでOptionalで返却を行うためのUtilクラス<br><br>
     * <p>
     * 基本的にNullableな値を得るときにはこのstaticインナークラスに委譲する
     */
    private static final class CapsFetcher {

        /**
         * @param entityCaps チェック対象のリトルメイド
         * @return 乗っかっているものの名前を返す(ボートに乗っているならBoatが帰ってくる)
         */
        static Optional<String> getRidingName(final IModelCaps entityCaps) {
            Object o = ModelCapsHelper.getCapsValue(entityCaps, caps_getRidingName);
            if (!(o instanceof String)) {
                return Optional.empty();
            }

            String casted = (String) o;
            if (casted.equals("")) {
                // LMM FP版固有のお座りとかは""で返されるようなのでnullとして返却
                return Optional.empty();
            }

            return Optional.of(casted);

        }

        /**
         * 必要がないので、他クラスからインスタンス化できないようにする
         * コンストラクタもprivateならEnumで良さそうだけど列挙要素がないEnumは何かやなのでクラスにした
         */
        private CapsFetcher() {
        }
    }

    /**
     *SR2と他のリトルメイドで利き手が違うっぽい？
     */
    private enum ArmArray{
        RIGHT(0),
        LEFT(1);

        private final int value;

        ArmArray(int value){
            this.value = value;
        }
    }


    /**
     * 拡大率制御用変数
     * ここのEnumを用意した・追加した物に変更することで全体の拡大率を変更することができる
     */
    private static final SizeRate sizeRate = SizeRate.ZERO_DOT950;

    @SuppressWarnings("unused")
    public ModelLittleMaid_ZeroDot333DangerNotSR2() {
        super();
    }

    @SuppressWarnings("unused")
    public ModelLittleMaid_ZeroDot333DangerNotSR2(float pSize) {
        super(pSize);
//        テクスチャがデフォルトでない場合(多分使わない)
//        super(pSize, 0.0f, 64, 64);
    }

    @SuppressWarnings("unused")
    public ModelLittleMaid_ZeroDot333DangerNotSR2(float pSize, float pYOffset, int pTextureWidth, int pTextureHeight) {
        super(pSize, pYOffset, pTextureWidth, pTextureHeight);
    }

    @Override
    public void initModel(float size, float yOffset) {
        initArmsArray();
        HeadMount.setRotationPoint(0.0F, -4.0F, 0.0F);
        HeadTop.setRotationPoint(0.0F, -13.0F, 0.0F);

        initBipedHead(size);
        initRightArm(size);
        initLeftArm(size);

        initBipedRightLeg(size);
        initBipedLeftLeg(size);
        initSkirt(size);

        initBipedBody(size);
        bipedTorso = new ModelRenderer(this);
        bipedNeck = new ModelRenderer(this);

        bipedPelvic = new ModelRenderer(this);
        bipedPelvic.setRotationPoint(0.0F, 7.0F, 0.0F);
        mainFrame = new ModelRenderer(this, 0, 0, sizeRate.axisX, sizeRate.axisY, sizeRate.axisZ);

        mainFrame.setRotationPoint(0.0F, 0.0F + yOffset + 8.0F, 0.0F);
        initAddChild();
        initSR2(size);
    }

    private void initSR2(float size) {
        eyeR = new ModelRenderer(this, 32, 19, sizeRate.axisX, sizeRate.axisY, sizeRate.axisZ);
        eyeR.addPlate(-4.0F, -5.0F, -4.001F, 4, 4, 0, size);
        eyeR.setRotationPoint(0.0F, 0.0F, 0.0F);
        eyeL = new ModelRenderer(this, 42, 19, sizeRate.axisX, sizeRate.axisY, sizeRate.axisZ);
        eyeL.addPlate(0.0F, -5.0F, -4.001F, 4, 4, 0, size);
        eyeL.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHead.addChild(eyeR);
        bipedHead.addChild(eyeL);
    }

    private void initBipedBody(float size) {
        bipedBody = new ModelRenderer(this, 32, 8, sizeRate.axisX, sizeRate.axisY, sizeRate.axisZ);
        bipedBody.addBox(-3.0F, 0.0F, -2.0F, 6, 7, 4, size);
        bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    private void initLeftArm(float size) {
        bipedLeftArm = new ModelRenderer(this, 56, 0, sizeRate.axisX, sizeRate.axisY, sizeRate.axisZ);
        bipedLeftArm.addBox(0.0F, -1.0F, -1.0F, 2, 8, 2, size);
        bipedLeftArm.setRotationPoint(3.0F, 1.5F, 0.0F);
    }

    private void initRightArm(float size) {
        bipedRightArm = new ModelRenderer(this, 48, 0, sizeRate.axisX, sizeRate.axisY, sizeRate.axisZ);
        bipedRightArm.addBox(-2.0F, -1.0F, -1.0F, 2, 8, 2, size);
        bipedRightArm.setRotationPoint(-3.0F, 1.5F, 0.0F);
    }

    private void initArmsArray() {
        // アーム配列は役割がよくわからんので触らないことにする
        this.Arms[ArmArray.RIGHT.value] = new ModelRenderer(this);
        this.Arms[ArmArray.RIGHT.value].setRotationPoint(-1.0F + sizeRate.rebirthAxisX, 5.0F - 4f * sizeRate.rebirthAxisY, -1.0F);
        this.Arms[ArmArray.LEFT.value] = new ModelRenderer(this);
        this.Arms[ArmArray.LEFT.value].setRotationPoint(1.0F - sizeRate.rebirthAxisX, 5.0F - 4f * sizeRate.rebirthAxisY, -1.0F);
        this.Arms[ArmArray.LEFT.value].isInvertX = true;
    }

    private void initBipedHead(float size) {
        bipedHead = new ModelRenderer(this, 0, 0, sizeRate.axisX, sizeRate.axisY, sizeRate.axisZ);

        // ここらへんシニョンとかカラー毎の髪パーツだと思う
        bipedHead.setTextureOffset(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, size);
        bipedHead.setTextureOffset(24, 0).
                addBox(-4.0F, 0.0F, 1.0F, 8, 4, 3, size);
        bipedHead.setTextureOffset(24, 18)
                .addBox(-4.995F, -7.0F, 0.2F, 1, 3, 3, size);
        bipedHead.setTextureOffset(24, 18)
                .addBox(3.995F, -7.0F, 0.2F, 1, 3, 3, size);
        bipedHead.setTextureOffset(52, 10)
                .addBox(-2.0F, -7.2F, 4.0F, 4, 4, 2, size);
        bipedHead.setTextureOffset(46, 20)
                .addBox(-1.5F, -6.8F, 4.0F, 3, 9, 3, size);
        bipedHead.setTextureOffset(58, 21)
                .addBox(-5.5F, -6.8F, 0.9F, 1, 8, 2, size);
        bipedHead.setMirror(true);
        bipedHead.setTextureOffset(58, 21)
                .addBox(4.5F, -6.8F, 0.9F, 1, 8, 2, size);


        bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    private void initBipedRightLeg(float size) {
        bipedRightLeg = new ModelRenderer(this, 32, 19, sizeRate.axisX, sizeRate.axisY, sizeRate.axisZ);
        bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 3, 9, 4, size);
        bipedRightLeg.setRotationPoint(-1.0F, 0.0F, 0.0F);
    }

    private void initBipedLeftLeg(float size) {
        bipedLeftLeg = new ModelRenderer(this, 32, 19, sizeRate.axisX, sizeRate.axisY, sizeRate.axisZ);
        bipedLeftLeg.setMirror(true);
        bipedLeftLeg.addBox(-1.0F, 0.0F, -2.0F, 3, 9, 4, size);
        bipedLeftLeg.setRotationPoint(1.0F, 0.0F, 0.0F);
    }

    private void initAddChild() {
        mainFrame.addChild(bipedTorso);
        bipedTorso.addChild(bipedNeck);
        bipedTorso.addChild(bipedBody);
        bipedTorso.addChild(bipedPelvic);
        bipedNeck.addChild(bipedHead);
        bipedNeck.addChild(bipedRightArm);
        bipedNeck.addChild(bipedLeftArm);
        bipedHead.addChild(HeadMount);
        bipedHead.addChild(HeadTop);
        bipedRightArm.addChild(Arms[0]);
        bipedLeftArm.addChild(Arms[1]);
        bipedPelvic.addChild(bipedRightLeg);
        bipedPelvic.addChild(bipedLeftLeg);
        bipedPelvic.addChild(Skirt);
    }

    private void initSkirt(float size) {
        Skirt = new ModelRenderer(this, 0, 16, sizeRate.axisX, sizeRate.axisY, sizeRate.axisZ);
        Skirt.addBox(-4.0F, -2.0F, -4.0F, 8, 8, 8, size);
        Skirt.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    /**
     * ここはエンティティ生成時や読み取り時に呼ばれるっぽい
     * ここの値を元に当たり判定が決定されているが、動的な変更は不可
     * <p>
     * 追記:極一部のモデルはリアルタイムに身長を変える機能を持っているようなので、「動的な変更は不可」は多分誤りです。
     *
     * @return メイドさんの身長
     */
    @Override
    public float getHeight() {
        return super.getHeight() * sizeRate.axisY;
    }

    @Override
    public float getWidth() {
        return super.getWidth() * sizeRate.axisX;
    }

//    @Override
//    public float getyOffset() {
//        return super.getyOffset() * sizeRate.axisY;
//    }
//
//    @Override
//    public float getMountedYOffset() {
//        // 何のメソッドかは分からないけどとりあえず調整して返す
//        return super.getMountedYOffset() * sizeRate.axisY;
//    }

    @Override
    public void setLivingAnimations(IModelCaps entityCaps, float limbSwing, float limbSwingAmount, float partialTickTime) {
        super.setLivingAnimations(entityCaps, limbSwing, limbSwingAmount, partialTickTime);

        float f3 = entityTicksExisted + partialTickTime + entityIdFactor;
        // 目パチ
        if (0 > mh_sin(f3 * 0.05F) + mh_sin(f3 * 0.13F) + mh_sin(f3 * 0.7F) + 2.55F) {
            eyeR.setVisible(true);
            eyeL.setVisible(true);
        } else {
            eyeR.setVisible(false);
            eyeL.setVisible(false);
        }
    }

    @Override
    public void setDefaultPause(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, IModelCaps entityCaps) {
        // 親クラスのsetDefault...(args*...)は意図的に呼んでいない
        setDefaultPause();

        // ちらつき防止変数
        final float minimumMoveValue = 0.001f;

        mainFrame.setRotationPoint(0.0F, 8.0F + 9.0f * sizeRate.rebirthAxisY, 0.0F);
        mainFrame.setRotateAngleDegX(0.0F);
        bipedTorso.setRotationPoint(0.0F, 0.0F, 0.0F).setRotateAngle(0.0F, 0.0F, 0.0F);
        bipedNeck.setRotationPoint(0.0F, 0.0F + 8.0f * sizeRate.rebirthAxisY, 0.0F).setRotateAngle(0.0F, 0.0F, 0.0F);
        bipedPelvic.setRotationPoint(0.0F, 7.0F, 0.0F).setRotateAngle(0.0F, 0.0F, 0.0F);
        bipedHead.setRotationPoint(0.0F + minimumMoveValue, 0.0F, 0.0F + minimumMoveValue);
        bipedHead.setRotateAngleDegY(netHeadYaw);
        bipedHead.setRotateAngleDegX(headPitch);
        bipedBody.setRotationPoint(0.0F, 0.0F + 7.0f * sizeRate.rebirthAxisY, 0.0F).setRotateAngle(0.0F, 0.0F, 0.0F);
        //bipedRightArm.setRotationPoint(-3.0F + 2.0f * sizeRate.rebirthAxisX, 1.6F - 2.0f * sizeRate.rebirthAxisY, 0.0F);

        //腕の角度Rと縮小後の座標x,yから、本来の位置x',y'はアフィン変換すれば求められると思うけど行列計算と角度推定が面倒なので後回し
        bipedRightArm.setRotationPoint(-3.0F + 3.0f * sizeRate.rebirthAxisX, 1.6F - 1.75f * sizeRate.rebirthAxisY, 0.0F);
        bipedRightArm.setRotateAngle(mh_cos(limbSwing * 0.6662F + 3.141593F) * 2.0F * limbSwingAmount * 0.5F, 0.0F, 0.0F);
        bipedLeftArm.setRotationPoint(3.0F - 3.0f * sizeRate.rebirthAxisX, 1.6F - 1.75f * sizeRate.rebirthAxisY, 0.0F);
        bipedLeftArm.setRotateAngle(mh_cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F, 0.0F, 0.0F);

        final float moveRotatePointLegX = 1.25f * sizeRate.rebirthAxisX;
        bipedRightLeg.setRotationPoint(-1.0F + moveRotatePointLegX, 0.0F, 0.0F);
        bipedRightLeg.setRotateAngle(mh_cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount, 0.0F, 0.0F);


        bipedLeftLeg.setRotationPoint(1.0F - moveRotatePointLegX, 0.0F, 0.0F);
        bipedLeftLeg.setRotateAngle(mh_cos(limbSwing * 0.6662F + 3.141593F) * 1.4F * limbSwingAmount, 0.0F, 0.0F);
        Skirt.setRotationPoint(0.0F, 0.0F, 0.0F).setRotateAngle(0.0F, 0.0F, 0.0F);

        if (aimedBow) {
            if (ModelCapsHelper.getCapsValueInt(entityCaps, caps_dominantArm) == 0) {
                eyeL.setVisible(true);
            } else {
                eyeR.setVisible(true);
            }
            return;
        }

        if (isRiding) {
            // 縮尺が違うメイドさんがお座りすると多分mainFrame自体が変動して埋まったりするのでその対策
            mainFrame.setRotationPointY(mainFrame.getRotationPointY() - 6.0f * sizeRate.rebirthAxisY);

            CapsFetcher.getRidingName(entityCaps).ifPresent(ridesName -> {
                if (ridesName.equals("Boat")) {
                    // ボート窒息は身長で決まるためこの処理でも防げない
                    mainFrame.setRotationPointY(mainFrame.getRotationPointY() - 6.0f * sizeRate.rebirthAxisY);
                }
            });
        }
    }

}
